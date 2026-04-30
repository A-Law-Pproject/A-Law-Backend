package com.service.alaw.platform.voice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.service.alaw.platform.voice.domain.document.VoiceAnalysisDocument;
import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.entity.VoiceRecordStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GET /api/v1/voice-records/{voiceRecordId}/analysis 통합 조회 응답.
 * mode가 FACT_CHECK이면 factCheck 필드에, VOICE_ONLY이면 voiceAnalysis 필드에 결과가 담긴다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VoiceRecordDetailResponse(
        Long voiceRecordId,
        String title,
        String jobId,
        String fileUrl,
        VoiceRecordStatus status,
        String mode,
        String transcript,
        Integer processingTimeMs,
        LocalDateTime createdAt,
        FactCheckResult factCheck,
        VoiceAnalysisResult voiceAnalysis
) {

    public static VoiceRecordDetailResponse ofFactCheck(VoiceRecord voiceRecord, VoiceFactCheckDocument doc) {
        return new VoiceRecordDetailResponse(
                voiceRecord.getVoiceRecordId(),
                voiceRecord.getTitle(),
                voiceRecord.getJobId(),
                voiceRecord.getFileUrl(),
                voiceRecord.getStatus(),
                "FACT_CHECK",
                doc.getTranscript(),
                doc.getProcessingTimeMs(),
                doc.getCreatedAt(),
                FactCheckResult.from(doc),
                null
        );
    }

    public static VoiceRecordDetailResponse ofVoiceOnly(VoiceRecord voiceRecord, VoiceAnalysisDocument doc) {
        return new VoiceRecordDetailResponse(
                voiceRecord.getVoiceRecordId(),
                voiceRecord.getTitle(),
                voiceRecord.getJobId(),
                voiceRecord.getFileUrl(),
                voiceRecord.getStatus(),
                "VOICE_ONLY",
                doc.getTranscript(),
                doc.getProcessingTimeMs(),
                doc.getCreatedAt(),
                null,
                VoiceAnalysisResult.from(doc)
        );
    }

    /** PENDING/FAILED 등 분석 결과 문서가 아직 없는 경우 */
    public static VoiceRecordDetailResponse ofPending(VoiceRecord voiceRecord) {
        return new VoiceRecordDetailResponse(
                voiceRecord.getVoiceRecordId(),
                voiceRecord.getTitle(),
                voiceRecord.getJobId(),
                voiceRecord.getFileUrl(),
                voiceRecord.getStatus(),
                voiceRecord.hasContract() ? "FACT_CHECK" : "VOICE_ONLY",
                null, null, null, null, null
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FactCheckResult(List<FactCheckItem> factCheckItems) {
        public static FactCheckResult from(VoiceFactCheckDocument doc) {
            List<FactCheckItem> items = doc.getFactCheckItems() != null
                    ? doc.getFactCheckItems().stream().map(FactCheckItem::from).toList()
                    : List.of();
            return new FactCheckResult(items);
        }

        public record FactCheckItem(String claim, String contractContent, boolean isMatch, String severity) {
            public static FactCheckItem from(VoiceFactCheckDocument.FactCheckItem item) {
                return new FactCheckItem(item.getClaim(), item.getContractContent(), item.isMatch(), item.getSeverity());
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record VoiceAnalysisResult(
            String summary,
            List<String> keyPoints,
            List<RiskItem> riskItems,
            List<SegmentItem> segments,
            List<AgreementItem> agreements
    ) {
        public static VoiceAnalysisResult from(VoiceAnalysisDocument doc) {
            return new VoiceAnalysisResult(
                    doc.getSummary(),
                    doc.getKeyPoints(),
                    doc.getRiskItems() != null
                            ? doc.getRiskItems().stream().map(RiskItem::from).toList()
                            : List.of(),
                    doc.getSegments() != null
                            ? doc.getSegments().stream().map(SegmentItem::from).toList()
                            : List.of(),
                    doc.getAgreements() != null
                            ? doc.getAgreements().stream().map(AgreementItem::from).toList()
                            : List.of()
            );
        }

        public record RiskItem(String riskType, String severity, String detail, String timestampStr) {
            public static RiskItem from(VoiceAnalysisDocument.RiskItem item) {
                return new RiskItem(item.getRiskType(), item.getSeverity(), item.getDetail(), item.getTimestampStr());
            }
        }

        public record SegmentItem(String id, Double startTime, Double endTime, String text, String speaker, String timestampStr) {
            public static SegmentItem from(VoiceAnalysisDocument.SegmentItem item) {
                return new SegmentItem(item.getId(), item.getStartTime(), item.getEndTime(), item.getText(), item.getSpeaker(), item.getTimestampStr());
            }
        }

        public record AgreementItem(String segmentId, String agreementType, String value, String context, String timestampStr) {
            public static AgreementItem from(VoiceAnalysisDocument.AgreementItem item) {
                return new AgreementItem(item.getSegmentId(), item.getAgreementType(), item.getValue(), item.getContext(), item.getTimestampStr());
            }
        }
    }
}
