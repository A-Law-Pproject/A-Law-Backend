package com.service.alaw.platform.voice.domain.entity;

import com.service.alaw.platform.BaseTimeEntity;
import com.service.alaw.platform.contract.domain.entity.Contract;
import com.service.alaw.platform.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voice_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voice_record_id")
    private Long voiceRecordId;

    /**
     * 계약서 연계 팩트체크 시에만 값을 가진다.
     * voice-only(계약서 없는 단독 분석) 시에는 null 이다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = true)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "job_id", length = 100)
    private String jobId;

    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoiceRecordStatus status;

    @Column(name = "analysis_id", length = 255)
    private String analysisId;

    @Builder
    private VoiceRecord(Contract contract, User user, String title, String jobId, String s3Key, String fileUrl) {
        this.contract = contract;
        this.user = user;
        this.title = title;
        this.jobId = jobId;
        this.s3Key = s3Key;
        this.fileUrl = fileUrl;
        this.status = VoiceRecordStatus.PENDING;
    }

    /** 계약서 연계 음성 레코드 생성 팩토리 메서드. */
    public static VoiceRecord of(Contract contract, User user, String title, String jobId, String s3Key, String fileUrl) {
        return VoiceRecord.builder()
                .contract(contract)
                .user(user)
                .title(title)
                .jobId(jobId)
                .s3Key(s3Key)
                .fileUrl(fileUrl)
                .build();
    }

    /** voice-only(계약서 없음) 음성 레코드 생성 팩토리 메서드. */
    public static VoiceRecord ofVoiceOnly(User user, String title, String jobId, String s3Key, String fileUrl) {
        return VoiceRecord.builder()
                .contract(null)
                .user(user)
                .title(title)
                .jobId(jobId)
                .s3Key(s3Key)
                .fileUrl(fileUrl)
                .build();
    }

    public void updateFile(String title, String jobId, String s3Key, String fileUrl) {
        this.title = title;
        this.jobId = jobId;
        this.s3Key = s3Key;
        this.fileUrl = fileUrl;
        this.status = VoiceRecordStatus.PENDING;
    }

    public void startAnalysis(String jobId) {
        this.jobId = jobId;
        this.status = VoiceRecordStatus.PENDING;
    }

    public void linkAnalysis(String analysisId) {
        this.analysisId = analysisId;
    }

    public void complete() {
        this.status = VoiceRecordStatus.COMPLETED;
    }

    public void fail() {
        this.status = VoiceRecordStatus.FAILED;
    }

    /** 계약서 연계 여부 확인. */
    public boolean hasContract() {
        return this.contract != null;
    }
}
