package com.service.alaw.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.service.alaw.common.exception.code.BaseCode;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.exception.code.CommonSuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ResponseEntity<ApiResponse<Void>> success() {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.SUCCESS));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.SUCCESS, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> retrieved(T data) {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.RETRIEVED, data));
    }

    public static ResponseEntity<ApiResponse<Void>> updated() {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.UPDATED));
    }

    public static <T> ResponseEntity<ApiResponse<T>> updated(T data) {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.UPDATED, data));
    }

    public static ResponseEntity<ApiResponse<Void>> deleted() {
        return ResponseEntity.ok().body(successWithCode(CommonSuccessCode.DELETED));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(successWithCode(CommonSuccessCode.CREATED, data));
    }

    public static ResponseEntity<ApiResponse<Void>> created() {
        return ResponseEntity.status(HttpStatus.CREATED).body(successWithCode(CommonSuccessCode.CREATED));
    }

    private static <T> ApiResponse<T> successWithCode(BaseCode successCode, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    private static ApiResponse<Void> successWithCode(BaseCode successCode) {
        return ApiResponse.<Void>builder()
                .success(true)
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, BaseCode errorCode) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .success(false)
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    public static <T> ApiResponse<T> error(BaseCode errorCode, String overrideMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(overrideMessage)
                .build();
    }
}
