package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.infra.messaging.VoiceRecordPublisher;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.voice.application.dto.VoiceAnalysisMessage;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
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
    private final S3UploadService s3UploadService;
    private final VoiceRecordPublisher voiceRecordPublisher;

    // POST /api/v1/contracts/{contractId}/voice-records — 계약서에 바로 연결하여 저장
    @Transactional
    public VoiceRecordResponse save(Long contractId, Long userId, String title, MultipartFile file) {
        Contract contract = contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        User user = contract.getUser();

        String jobId = UUID.randomUUID().toString();
        String s3Key = s3UploadService.upload(file);
        String fileUrl = s3UploadService.getFileUrl(s3Key);

        VoiceRecord voiceRecord = voiceRecordRepository
                .findByContract_ContractIdAndUser_UserId(contractId, userId)
                .map(existing -> {
                    s3UploadService.delete(existing.getS3Key());
                    existing.updateFile(title, jobId, s3Key, fileUrl);
                    return existing;
                })
                .orElseGet(() -> VoiceRecord.of(contract, user, title, jobId, s3Key, fileUrl));

        VoiceRecord saved = voiceRecordRepository.save(voiceRecord);

        log.info("[VoiceRecord] 업로드 완료 - voiceRecordId={}, jobId={}", saved.getVoiceRecordId(), jobId);
        return VoiceRecordResponse.from(saved);
    }

    // POST /api/v1/voice-records — 계약서 연결 없이 저장
    @Transactional
    public VoiceRecordResponse saveWithoutContract(Long userId, String title, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        String jobId = UUID.randomUUID().toString();
        String s3Key = s3UploadService.upload(file);
        String fileUrl = s3UploadService.getFileUrl(s3Key);

        VoiceRecord voiceRecord = VoiceRecord.of(null, user, title, jobId, s3Key, fileUrl);
        VoiceRecord saved = voiceRecordRepository.save(voiceRecord);

        log.info("[VoiceRecord] 업로드 완료 (계약서 미연결) - voiceRecordId={}", saved.getVoiceRecordId());
        return VoiceRecordResponse.from(saved);
    }

    // GET /api/v1/voice-records — 전체 녹음 목록 조회
    @Transactional(readOnly = true)
    public List<VoiceRecordResponse> findAll(Long userId) {
        return voiceRecordRepository.findAllByUser_UserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(VoiceRecordResponse::from)
                .toList();
    }

    // GET /api/v1/contracts/{contractId}/voice-record — 특정 계약서의 녹음 조회
    @Transactional(readOnly = true)
    public VoiceRecordResponse findByContract(Long contractId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository
                .findByContract_ContractIdAndUser_UserId(contractId, userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));
        return VoiceRecordResponse.from(voiceRecord);
    }

    // DELETE /api/v1/voice-records/{voiceRecordId} — 녹음 삭제
    @Transactional
    public void delete(Long voiceRecordId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(v -> v.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        s3UploadService.delete(voiceRecord.getS3Key());
        voiceRecordRepository.delete(voiceRecord);

        log.info("[VoiceRecord] 삭제 완료 - voiceRecordId={}", voiceRecordId);
    }

    // POST /api/v1/voice-records/{voiceRecordId}/analyze — 분석 시작
    @Transactional
    public VoiceAnalyzeResponse analyze(Long voiceRecordId, Long userId, Long contractId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(v -> v.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        if (voiceRecord.getContract() == null) {
            if (contractId == null) {
                throw new ContractNotFoundException(CommonErrorCode.NOT_FOUND);
            }
            Contract contract = contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                    .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));
            voiceRecord.linkContract(contract);
        }

        String newJobId = UUID.randomUUID().toString();
        voiceRecord.startAnalysis(newJobId);
        voiceRecordRepository.save(voiceRecord);

        voiceRecordPublisher.publish(new VoiceAnalysisMessage(
                voiceRecord.getVoiceRecordId(),
                voiceRecord.getContract().getContractId(),
                userId,
                newJobId,
                voiceRecord.getS3Key(),
                voiceRecord.getContract().getRawText(),
                voiceRecord.getTranscript()
        ));

        log.info("[VoiceRecord] 분석 요청 - voiceRecordId={}, jobId={}", voiceRecordId, newJobId);
        return new VoiceAnalyzeResponse(voiceRecordId, newJobId);
    }
}
