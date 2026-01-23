package com.service.alaw.contractmanagement.entity;

import com.service.alaw.platform.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "analysis_id")
    private String analysisId;

    @Column(nullable = false)
    private String title;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(nullable = false)
    private boolean bookmark;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Builder
    public Contract(Long memberId, String title, String fileUrl, ContractType contractType) {
        this.memberId = memberId;
        this.title = title;
        this.fileUrl = fileUrl;
        this.contractType = contractType;
        this.bookmark = false;
        this.status = ContractStatus.PENDING;
    }

    // 비즈니스 로직
    public void update(String title) {
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
