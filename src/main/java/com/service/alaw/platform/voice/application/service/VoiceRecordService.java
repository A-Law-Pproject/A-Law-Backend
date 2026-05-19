package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.FastApiException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.infra.ai.VoiceAnalysisClient;
import com.service.alaw.infra.messaging.VoiceRecordPublisher;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.voice.application.dto.VoiceAnalysisMessage;
import com.service.alaw.platform.voice.application.dto.VoiceAnalysisResponse;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeStartResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import com.service.alaw.platform.voice.domain.document.VoiceAnalysisDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.repository.VoiceAnalysisRepository;
import com.service.alaw.platform.voice.domain.repository.VoiceRecordRepository;
import com.service.alaw.platform.user.domain.entity.User;
import com.service.alaw.platform.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceRecordService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final VoiceRecordRepository voiceRecordRepository;
    private final VoiceAnalysisRepository voiceAnalysisRepository;
    private final S3UploadService s3UploadService;
    private final VoiceRecordPublisher voiceRecordPublisher;
    private final VoiceAnalysisClient voiceAnalysisClient;

    @Transactional(readOnly = true)
    public List<VoiceRecordResponse> getMyVoiceRecords(Long userId) {
        return voiceRecordRepository.findByUser_UserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(VoiceRecordResponse::from)
                .toList();
    }

    /**
     * 계약서 연계 음성 레코드를 저장한다.
     * contractId 가 null 이면 voice-only 레코드로 저장한다.
     */
    @Transactional
    public VoiceRecordResponse save(Long contractId, Long userId, String title, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        String jobId = UUID.randomUUID().toString();
        String s3Key = s3UploadService.upload(file);
        String fileUrl = s3UploadService.getFileUrl(s3Key);

        VoiceRecord voiceRecord;

        if (contractId != null) {
            // 계약서 연계 경로
            Contract contract = contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                    .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

            voiceRecord = voiceRecordRepository
                    .findByContract_ContractIdAndUser_UserId(contractId, userId)
                    .map(existing -> {
                        existing.updateFile(title, jobId, s3Key, fileUrl);
                        return existing;
                    })
                    .orElseGet(() -> VoiceRecord.of(contract, user, title, jobId, s3Key, fileUrl));
        } else {
            // voice-only 경로: 계약서 없이 레코드 생성
            voiceRecord = VoiceRecord.ofVoiceOnly(user, title, jobId, s3Key, fileUrl);
        }

        VoiceRecord saved = voiceRecordRepository.save(voiceRecord);
        log.info("[VoiceRecord] 업로드 완료 - voiceRecordId={}, contractId={}, jobId={}",
                saved.getVoiceRecordId(), contractId, jobId);

        return VoiceRecordResponse.from(saved);
    }

    /**
     * 음성 분석을 트리거한다.
     * - 계약서 연계 레코드: RabbitMQ 비동기 팩트체크 → {jobId, async=true} 반환
     * - voice-only 레코드: FastAPI 직접 HTTP 호출 → {jobId, async=false, result} 반환
     */
    @Transactional
    public VoiceAnalyzeStartResponse analyze(Long voiceRecordId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(v -> v.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        String newJobId = UUID.randomUUID().toString();
        voiceRecord.startAnalysis(newJobId);
        voiceRecordRepository.save(voiceRecord);

        if (voiceRecord.hasContract()) {
            // 계약서 연계 팩트체크: RabbitMQ 비동기 발행 (rawText 미포함)
            voiceRecordPublisher.publish(new VoiceAnalysisMessage(
                    voiceRecord.getVoiceRecordId(),
                    voiceRecord.getContract().getContractId(),
                    userId,
                    newJobId,
                    voiceRecord.getS3Key()
            ));
            log.info("[VoiceRecord] 팩트체크 비동기 요청 - voiceRecordId={}, jobId={}", voiceRecordId, newJobId);
            return new VoiceAnalyzeStartResponse(newJobId, true, null);
        }

        // voice-only: FastAPI 직접 HTTP 호출
        log.info("[VoiceRecord] voice-only 분석 시작 - voiceRecordId={}, jobId={}", voiceRecordId, newJobId);
        VoiceAnalysisResponse analysisResponse = voiceAnalysisClient.analyzeVoiceOnly(
                voiceRecord.getS3Key(),
                String.valueOf(voiceRecord.getVoiceRecordId())
        );

        if (analysisResponse == null) {
            voiceRecord.fail();
            voiceRecordRepository.save(voiceRecord);
            throw new FastApiException("FastAPI 응답이 null 입니다.");
        }

        if (!analysisResponse.success()) {
            voiceRecord.fail();
            voiceRecordRepository.save(voiceRecord);
            log.warn("[VoiceRecord] voice-only 분석 실패 - voiceRecordId={}, error={}",
                    voiceRecordId, analysisResponse.errorMessage());
            return new VoiceAnalyzeStartResponse(newJobId, false, analysisResponse);
        }

        // MongoDB 에 분석 결과 저장
        VoiceAnalysisDocument doc = VoiceAnalysisDocument.from(voiceRecordId, analysisResponse);
        VoiceAnalysisDocument saved = voiceAnalysisRepository.save(doc);
        voiceRecord.complete();
        voiceRecord.linkAnalysis(saved.getId());
        voiceRecordRepository.save(voiceRecord);

        log.info("[VoiceRecord] voice-only 분석 완료 - voiceRecordId={}, analysisId={}",
                voiceRecordId, saved.getId());
        return new VoiceAnalyzeStartResponse(newJobId, false, analysisResponse);
    }
}
