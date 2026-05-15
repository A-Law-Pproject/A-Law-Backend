package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.voice.application.dto.VoiceFactCheckResponse;
import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.repository.VoiceFactCheckRepository;
import com.service.alaw.platform.voice.domain.repository.VoiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoiceFactCheckQueryService {

    private final VoiceRecordRepository voiceRecordRepository;
    private final VoiceFactCheckRepository voiceFactCheckRepository;

    public VoiceFactCheckResponse getFactCheck(Long contractId, Long voiceRecordId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(vr -> vr.getContract() != null && vr.getContract().getContractId().equals(contractId))
                .filter(vr -> vr.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        VoiceFactCheckDocument document = voiceFactCheckRepository.findByVoiceRecordId(voiceRecordId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        return VoiceFactCheckResponse.of(document, voiceRecord.getStatus());
    }
}
