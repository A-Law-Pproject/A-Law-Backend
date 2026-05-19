package com.service.alaw.platform.contract.application.dto.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.service.alaw.platform.contract.domain.document.ContractAnalysisDocument;
import com.service.alaw.platform.contract.domain.entity.AnalysisJob;
import com.service.alaw.platform.contract.domain.entity.AnalysisJobStatus;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContractAnalysisDetailResponse(
    String jobId,
    Long contractId,
    AnalysisJobStatus status,
    Long processingTimeMs,
    String errorMessage,
    LocalDateTime createdAt,
    SummaryResult summary,
    RiskAnalysisResult riskAnalysis) {

  public static ContractAnalysisDetailResponse ofCompleted(
      AnalysisJob job, ContractAnalysisDocument document) {
    return new ContractAnalysisDetailResponse(
        job.getJobId(),
        job.getContractId(),
        AnalysisJobStatus.COMPLETED,
        document.getProcessingTimeMs() != null
            ? document.getProcessingTimeMs().longValue()
            : job.getProcessingTimeMs(),
        null,
        document.getCreatedAt(),
        SummaryResult.from(document),
        RiskAnalysisResult.from(document));
  }

  public static ContractAnalysisDetailResponse ofFailed(AnalysisJob job) {
    return new ContractAnalysisDetailResponse(
        job.getJobId(),
        job.getContractId(),
        AnalysisJobStatus.FAILED,
        job.getProcessingTimeMs(),
        job.getErrorMessage(),
        job.getCreatedDate(),
        null,
        null);
  }

  public static ContractAnalysisDetailResponse ofPending(AnalysisJob job) {
    return new ContractAnalysisDetailResponse(
        job.getJobId(),
        job.getContractId(),
        job.getStatus(),
        job.getProcessingTimeMs(),
        job.getErrorMessage(),
        job.getCreatedDate(),
        null,
        null);
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record SummaryResult(String title, String summaryText, List<String> keyTerms) {

    public static SummaryResult from(ContractAnalysisDocument document) {
      return new SummaryResult(
          document.getSummaryTitle(), document.getSummaryText(), safeList(document.getKeyTerms()));
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record RiskAnalysisResult(
      Integer totalClauses,
      Integer riskCount,
      Integer cautionCount,
      Integer safetyCount,
      Double riskPercentage,
      List<ClauseResult> clauseResults) {

    public static RiskAnalysisResult from(ContractAnalysisDocument document) {
      return new RiskAnalysisResult(
          document.getTotalClauses(),
          document.getRiskCount(),
          document.getCautionCount(),
          document.getSafetyCount(),
          document.getRiskPercentage(),
          document.getClauseResults() != null
              ? document.getClauseResults().stream().map(ClauseResult::from).toList()
              : List.of());
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record ClauseResult(
      String clauseTitle,
      String clauseContent,
      String riskLevel,
      String legalReference,
      String relatedWork,
      String reasoningSummary,
      String category) {

    public static ClauseResult from(ContractAnalysisDocument.ClauseResult document) {
      return new ClauseResult(
          document.getClauseTitle(),
          document.getClauseContent(),
          document.getRiskLevel(),
          document.getLegalReference(),
          document.getRelatedWork(),
          document.getReasoningSummary(),
          document.getCategory());
    }
  }

  private static <T> List<T> safeList(List<T> values) {
    return values != null ? values : List.of();
  }
}
