package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.infra.messaging.VoiceRecordPublisher;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.contract.domain.repository.ContractRepository;
import com.service.alaw.platform.voice.application.dto.VoiceAnalysisMessage;
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

    @Transactional
    public VoiceRecordResponse save(Long contractId, Long userId, String title, MultipartFile file) {
        Contract contract = contractRepository.findByContractIdAndUser_UserId(contractId, userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        String jobId = UUID.randomUUID().toString();
        String s3Key = s3UploadService.upload(file);
        String fileUrl = s3UploadService.getFileUrl(s3Key);

        VoiceRecord voiceRecord = voiceRecordRepository
                .findByContract_ContractIdAndUser_UserId(contractId, userId)
                .map(existing -> {
                    existing.updateFile(title, jobId, s3Key, fileUrl);
                    return existing;
                })
                .orElseGet(() -> VoiceRecord.of(contract, user, title, jobId, s3Key, fileUrl));

        VoiceRecord saved = voiceRecordRepository.save(voiceRecord);

        voiceRecordPublisher.publish(new VoiceAnalysisMessage(
                saved.getVoiceRecordId(),
                contractId,
                userId,
                jobId,
                s3Key,
                contract.getRawText()
        ));

        log.info("[VoiceRecord] 업로드 완료 - voiceRecordId={}, jobId={}", saved.getVoiceRecordId(), jobId);
        return VoiceRecordResponse.from(saved);
    }
}
