package com.service.alaw.platform.voice.domain.document;

import com.service.alaw.platform.voice.application.dto.VoiceAnalysisResponse;
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

/**
 * voice-only(계약서 비연계) 분석 결과를 저장하는 MongoDB Document.
 * 컬렉션: voice_analysis_results
 */
@Getter
@Builder
@Document(collection = "voice_analysis_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VoiceAnalysisDocument {

    @Id
    private String id;

    @Indexed
    @Field("voice_record_id")
    private Long voiceRecordId;

    @Field("transcript")
    private String transcript;

    @Field("summary")
    private String summary;

    @Field("key_points")
    private List<String> keyPoints;

    @Field("risk_items")
    private List<RiskItem> riskItems;

    @Field("segments")
    private List<SegmentItem> segments;

    @Field("agreements")
    private List<AgreementItem> agreements;

    @Field("file_hash")
    private String fileHash;

    @Field("s3_key")
    private String s3Key;

    @Field("processing_time_ms")
    private Integer processingTimeMs;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    /** FastAPI 응답으로부터 Document 를 생성한다. */
    public static VoiceAnalysisDocument from(Long voiceRecordId, VoiceAnalysisResponse response) {
        VoiceAnalysisResponse.VoiceAnalysisSummary sum = response.summary();
        VoiceAnalysisResponse.VoiceAudioMeta meta = response.audioMeta();

        return VoiceAnalysisDocument.builder()
                .voiceRecordId(voiceRecordId)
                .transcript(response.transcript())
                .summary(sum != null ? sum.summary() : null)
                .keyPoints(sum != null ? sum.keyPoints() : List.of())
                .riskItems(sum != null && sum.riskItems() != null
                        ? sum.riskItems().stream().map(RiskItem::from).toList()
                        : List.of())
                .segments(response.segments() != null
                        ? response.segments().stream().map(SegmentItem::from).toList()
                        : List.of())
                .agreements(response.agreements() != null
                        ? response.agreements().stream().map(AgreementItem::from).toList()
                        : List.of())
                .fileHash(meta != null ? meta.fileHash() : null)
                .s3Key(meta != null ? meta.s3Key() : null)
                .processingTimeMs(response.processingTimeMs())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RiskItem {
        @Field("risk_type")  private String riskType;
        @Field("severity")   private String severity;
        @Field("detail")     private String detail;
        @Field("timestamp_str") private String timestampStr;

        public static RiskItem from(VoiceAnalysisResponse.VoiceRiskItem dto) {
            return RiskItem.builder()
                    .riskType(dto.riskType())
                    .severity(dto.severity())
                    .detail(dto.detail())
                    .timestampStr(dto.timestampStr())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SegmentItem {
        @Field("id")           private String id;
        @Field("start_time")   private Double startTime;
        @Field("end_time")     private Double endTime;
        @Field("text")         private String text;
        @Field("speaker")      private String speaker;
        @Field("timestamp_str") private String timestampStr;

        public static SegmentItem from(VoiceAnalysisResponse.SegmentResult dto) {
            return SegmentItem.builder()
                    .id(dto.id())
                    .startTime(dto.startTime())
                    .endTime(dto.endTime())
                    .text(dto.text())
                    .speaker(dto.speaker())
                    .timestampStr(dto.timestampStr())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AgreementItem {
        @Field("segment_id")      private String segmentId;
        @Field("agreement_type")  private String agreementType;
        @Field("value")           private String value;
        @Field("context")         private String context;
        @Field("timestamp_str")   private String timestampStr;

        public static AgreementItem from(VoiceAnalysisResponse.AgreementItem dto) {
            return AgreementItem.builder()
                    .segmentId(dto.segmentId())
                    .agreementType(dto.agreementType())
                    .value(dto.value())
                    .context(dto.context())
                    .timestampStr(dto.timestampStr())
                    .build();
        }
    }
}
