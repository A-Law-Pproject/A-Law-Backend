package com.service.alaw.platform.voice.application.dto;

import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.entity.VoiceRecordStatus;

import java.time.LocalDateTime;

public record VoiceRecordResponse(
        Long voiceRecordId,
        String jobId,
        String fileUrl,
        VoiceRecordStatus status,
        LocalDateTime createdAt
) {
    public static VoiceRecordResponse from(VoiceRecord voiceRecord) {
        return new VoiceRecordResponse(
                voiceRecord.getVoiceRecordId(),
                voiceRecord.getJobId(),
                voiceRecord.getFileUrl(),
                voiceRecord.getStatus(),
                voiceRecord.getCreatedDate()
        );
    }
}
