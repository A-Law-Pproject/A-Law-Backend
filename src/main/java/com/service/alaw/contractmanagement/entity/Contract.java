package com.service.alaw.contractmanagement.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "contracts",
    indexes = {
        @Index(name = "idx_contract_user", columnList = "userId"),
        @Index(name = "idx_contract_user_deleted", columnList = "userId, deleted")
    }
)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String fileUrl;

    @Lob
    private String content;

    @Column(nullable = false)
    private boolean bookmarked = false;

    @Column(nullable = false)
    private boolean deleted = false;

    private Instant createdAt;
    private Instant updatedAt;

    protected Contract() {}

    public Contract(Long userId, String title, String fileUrl, String content) {
        this.userId = userId;
        this.title = title;
        this.fileUrl = fileUrl;
        this.content = content;
    }

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    // getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getFileUrl() { return fileUrl; }
    public String getContent() { return content; }
    public boolean isBookmarked() { return bookmarked; }
    public boolean isDeleted() { return deleted; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // domain behavior
    public void updateTitle(String title) { this.title = title; }
    public void setBookmarked(boolean bookmarked) { this.bookmarked = bookmarked; }
    public void softDelete() { this.deleted = true; }
}
