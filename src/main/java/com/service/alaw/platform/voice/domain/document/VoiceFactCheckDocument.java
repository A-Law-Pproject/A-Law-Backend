package com.service.alaw.platform.voice.domain.document;

import com.service.alaw.platform.voice.application.dto.VoiceFactCheckResultMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(collection = "voice_fact_checks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VoiceFactCheckDocument {

    @Id
    private String id;

    @Indexed
    @Field("voice_record_id")
    private Long voiceRecordId;

    @Field("contract_id")
    private Long contractId;

    @Field("job_id")
    private String jobId;

    @Field("transcript")
    private String transcript;

    @Field("fact_check_items")
    private List<FactCheckItem> factCheckItems;

    @Field("processing_time_ms")
    private Integer processingTimeMs;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    public static VoiceFactCheckDocument from(VoiceFactCheckResultMessage message) {
        return from(message, message.transcript());
    }

    public static VoiceFactCheckDocument from(VoiceFactCheckResultMessage message, String transcript) {
        return VoiceFactCheckDocument.builder()
                .voiceRecordId(message.voiceRecordId())
                .contractId(message.contractId())
                .jobId(message.jobId())
                .transcript(transcript)
                .factCheckItems(
                        message.factCheckItems() != null
                                ? message.factCheckItems().stream().map(FactCheckItem::from).toList()
                                : List.of()
                )
                .processingTimeMs(message.processingTimeMs())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FactCheckItem {

        @Field("claim")
        private String claim;

        @Field("contract_content")
        private String contractContent;

        @Field("is_match")
        private boolean isMatch;

        @Field("severity")
        private String severity;

        public static FactCheckItem from(VoiceFactCheckResultMessage.FactCheckItemDto dto) {
            return FactCheckItem.builder()
                    .claim(dto.claim())
                    .contractContent(dto.contractContent())
                    .isMatch(dto.isMatch())
                    .severity(dto.severity())
                    .build();
        }
    }
}
