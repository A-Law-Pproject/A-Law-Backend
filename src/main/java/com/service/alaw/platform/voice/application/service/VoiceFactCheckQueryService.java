package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.NotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.platform.voice.application.dto.VoiceRecordDetailResponse;
import com.service.alaw.platform.voice.domain.document.VoiceAnalysisDocument;
import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.entity.VoiceRecordStatus;
import com.service.alaw.platform.voice.domain.repository.VoiceAnalysisRepository;
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
    private final VoiceAnalysisRepository voiceAnalysisRepository;

    public VoiceRecordDetailResponse getVoiceDetail(Long voiceRecordId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(vr -> vr.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));

        if (voiceRecord.getStatus() != VoiceRecordStatus.COMPLETED) {
            return VoiceRecordDetailResponse.ofPending(voiceRecord);
        }

        if (voiceRecord.hasContract()) {
            VoiceFactCheckDocument doc = voiceFactCheckRepository.findByVoiceRecordId(voiceRecordId)
                    .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
            return VoiceRecordDetailResponse.ofFactCheck(voiceRecord, doc);
        }

        VoiceAnalysisDocument doc = voiceAnalysisRepository.findByVoiceRecordId(voiceRecordId)
                .orElseThrow(() -> new NotFoundException(CommonErrorCode.NOT_FOUND));
        return VoiceRecordDetailResponse.ofVoiceOnly(voiceRecord, doc);
    }
}
