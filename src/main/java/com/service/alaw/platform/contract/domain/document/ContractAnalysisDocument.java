package com.service.alaw.platform.contract.domain.document;

import com.service.alaw.platform.contract.application.dto.analysis.AnalysisResultMessage;
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
@Document(collection = "contract_analysis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractAnalysisDocument {

    @Id private String id;

    @Field("contract_id")
    private Long contractId;

    @Indexed
    @Field("s3_key")
    private String s3Key;

    @Indexed
    @Field("job_id")
    private String jobId;

    // ─── Summary ────────────────────────────────────────────────────────────

    @Field("summary_title")
    private String summaryTitle;

    @Field("summary_text")
    private String summaryText;

    @Field("key_terms")
    private List<String> keyTerms;

    // ─── Risk Analysis ───────────────────────────────────────────────────────

    @Field("total_clauses")
    private Integer totalClauses;

    @Field("risk_count")
    private Integer riskCount;

    @Field("caution_count")
    private Integer cautionCount;

    @Field("safety_count")
    private Integer safetyCount;

    @Field("risk_percentage")
    private Double riskPercentage;

    @Field("clause_results")
    private List<ClauseResult> clauseResults;

    @Field("processing_time_ms")
    private Integer processingTimeMs;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    /** FastAPI 결과 메시지로부터 생성 */
    public static ContractAnalysisDocument from(AnalysisResultMessage message) {
        AnalysisResultMessage.SummaryDto s = message.summary();
        AnalysisResultMessage.RiskAnalysisDto r = message.riskAnalysis();

        return ContractAnalysisDocument.builder()
                .contractId(message.contractId())
                .jobId(message.jobId())
                .summaryTitle(s != null ? s.title() : null)
                .summaryText(s != null ? s.summaryText() : null)
                .keyTerms(s != null ? s.keyTerms() : null)
                .totalClauses(r != null ? r.totalClauses() : null)
                .riskCount(r != null ? r.riskCount() : null)
                .cautionCount(r != null ? r.cautionCount() : null)
                .safetyCount(r != null ? r.safetyCount() : null)
                .riskPercentage(r != null ? r.riskPercentage() : null)
                .clauseResults(r != null ? r.clauseResults().stream().map(ClauseResult::from).toList() : null)
                .processingTimeMs(message.processingTimeMs())
                .build();
    }

    /** s3Key 기반 생성 (s3Key로 contractId를 특정할 수 없는 경우) */
    public static ContractAnalysisDocument fromS3Key(String s3Key, AnalysisResultMessage message) {
        ContractAnalysisDocument doc = from(message);
        return ContractAnalysisDocument.builder()
                .s3Key(s3Key)
                .contractId(doc.contractId)
                .jobId(doc.jobId)
                .summaryTitle(doc.summaryTitle)
                .summaryText(doc.summaryText)
                .keyTerms(doc.keyTerms)
                .totalClauses(doc.totalClauses)
                .riskCount(doc.riskCount)
                .cautionCount(doc.cautionCount)
                .safetyCount(doc.safetyCount)
                .riskPercentage(doc.riskPercentage)
                .clauseResults(doc.clauseResults)
                .processingTimeMs(doc.processingTimeMs)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClauseResult {
        private String clauseTitle;
        private String clauseContent;
        private String riskLevel;
        private String legalReference;
        private String reasoningSummary;
        private String category;

        public static ClauseResult from(AnalysisResultMessage.ClauseDto dto) {
            return ClauseResult.builder()
                    .clauseTitle(dto.clauseTitle())
                    .clauseContent(dto.clauseContent())
                    .riskLevel(dto.riskLevel())
                    .legalReference(dto.legalReference())
                    .reasoningSummary(dto.reasoningSummary())
                    .category(dto.category())
                    .build();
        }
    }
}
