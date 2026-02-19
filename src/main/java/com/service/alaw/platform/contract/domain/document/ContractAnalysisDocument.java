package com.service.alaw.platform.contract.domain.document;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@Document(collection = "contract_analysis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractAnalysisDocument {

  @Id private String id;

  @Field("analysis_id")
  private Long analysisId;

  @Field("contract_id")
  private Long contractId;

  @Field("summary")
  private String summary;

  @Field("risk_score")
  private Integer riskScore;

  @Field("risk_details")
  private String riskDetails;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;

  public static ContractAnalysisDocument of(
      Long contractId, String summary, Integer riskScore, String riskDetails) {
    return ContractAnalysisDocument.builder()
        .contractId(contractId)
        .summary(summary)
        .riskScore(riskScore)
        .riskDetails(riskDetails)
        .build();
  }

  public void updateAnalysis(String summary, Integer riskScore, String riskDetails) {
    this.summary = summary;
    this.riskScore = riskScore;
    this.riskDetails = riskDetails;
  }
}
