package com.service.alaw.platform;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseTimeDocument {

//    @Id
//    private String id;

    // 생성일시
    @CreatedDate
    private LocalDateTime createdAt;

    // 수정일시
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
