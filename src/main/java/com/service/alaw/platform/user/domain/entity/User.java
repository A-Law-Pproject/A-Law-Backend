package com.service.alaw.platform.user.domain.entity;

import com.service.alaw.platform.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_delete", nullable = false)
    private IsDelete isDelete;


    @Builder
    protected User(String name, UserRole role, Provider provider, String providerId, String profileImage) {
        this.name = name;
        this.role = role != null ? role : UserRole.User;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
        this.isDelete = IsDelete.N;
    }

    public static User of(String name, Provider provider, String providerId, String profileImage) {
        return User.builder()
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .build();
    }

    public void delete() {
        this.isDelete = IsDelete.Y;
    }

    public void update(String name, String profileImage) {
        if (name != null) {
            this.name = name;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

    public boolean isDeleted() {
        return this.isDelete.isDeleted();
    }
}
