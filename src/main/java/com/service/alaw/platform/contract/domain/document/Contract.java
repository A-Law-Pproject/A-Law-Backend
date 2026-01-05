package com.service.alaw.platform.contract.domain.document;

import com.service.alaw.platform.BaseTimeEntity;
import com.service.alaw.platform.user.domain.entity.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "contracts", indexes = @Index(name = "idx_user_email", columnList = "email", unique = true))
public class Contract extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String fileUrl;

    /// 빌더 생성자
//    @Builder
//    protected Contract(String name, String imageUrl, String nickname, String email, String password, UserRole role) {
//        this.name = name;
//        this.imageUrl = imageUrl;
//        this.nickname = nickname;
//        this.email = email;
//        this.password = password;
//        this.role = UserRole.MEMBER;    /// 기본은 다 유저
//        this.deleted = false;
//    }

    /// 정적 팩토리 메서드

    /// 비즈니스 로직
    /// 삭제


    /// 업데이트

}
