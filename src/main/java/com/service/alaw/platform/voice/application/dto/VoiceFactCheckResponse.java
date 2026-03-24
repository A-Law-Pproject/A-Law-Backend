package com.service.alaw.platform.voice.application.dto;

import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecordStatus;

import java.time.LocalDateTime;
import java.util.List;

public record VoiceFactCheckResponse(
        Long voiceRecordId,
        String transcript,
        List<FactCheckItem> factCheckItems,
        VoiceRecordStatus status,
        LocalDateTime createdAt
) {
    public static VoiceFactCheckResponse of(
            VoiceFactCheckDocument document,
            VoiceRecordStatus status
    ) {
        return new VoiceFactCheckResponse(
                document.getVoiceRecordId(),
                document.getTranscript(),
                document.getFactCheckItems().stream().map(FactCheckItem::from).toList(),
                status,
                document.getCreatedAt()
        );
    }

    public record FactCheckItem(
            String claim,
            String contractContent,
            boolean isMatch,
            String severity
    ) {
        public static FactCheckItem from(VoiceFactCheckDocument.FactCheckItem item) {
            return new FactCheckItem(
                    item.getClaim(),
                    item.getContractContent(),
                    item.isMatch(),
                    item.getSeverity()
            );
        }
    }
}
