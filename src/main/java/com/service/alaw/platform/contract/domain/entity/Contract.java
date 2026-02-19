package com.service.alaw.platform.contract.domain.entity;

import com.service.alaw.platform.BaseTimeEntity;
import com.service.alaw.platform.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "contracts")
public class Contract extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "contract_id")
  private Long contractId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "analysis_id", length = 255)
  private String analysisId;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "file_url", length = 255)
  private String fileUrl;

  @Column(name = "bookmark", nullable = false)
  private boolean bookmark;

  @Enumerated(EnumType.STRING)
  @Column(name = "contract_type")
  private ContractType contractType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ContractStatus status;

  @Column(name = "raw_text", columnDefinition = "TEXT")
  private String rawText;

  @Builder
  protected Contract(User user, String title, String fileUrl, ContractType contractType) {
    this.user = user;
    this.title = title;
    this.fileUrl = fileUrl;
    this.contractType = contractType;
    this.bookmark = false;
    this.status = ContractStatus.PENDING;
  }

  public static Contract of(User user, String title, String fileUrl, ContractType contractType) {
    return Contract.builder()
        .user(user)
        .title(title)
        .fileUrl(fileUrl)
        .contractType(contractType)
        .build();
  }

  // 비즈니스 로직
  public void updateTitle(String title) {
    if (title != null) {
      this.title = title;
    }
  }

  public void bookmark() {
    this.bookmark = true;
  }

  public void unbookmark() {
    this.bookmark = false;
  }

  public void toggleBookmark() {
    this.bookmark = !this.bookmark;
  }

  public void updateStatus(ContractStatus status) {
    this.status = status;
  }

  public void updateAnalysisId(String analysisId) {
    this.analysisId = analysisId;
  }

  public void updateRawText(String rawText) {
    this.rawText = rawText;
  }
}
