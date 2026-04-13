package com.service.alaw.common.exception;

import static org.springframework.http.HttpStatus.*;

import com.service.alaw.common.exception.code.BaseCode;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return build(ex.getErrorCode(), NOT_FOUND, ex);
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        return build(ex.getErrorCode(), CONFLICT, ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return build(ex.getErrorCode(), BAD_REQUEST, ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return build(ex.getErrorCode(), UNAUTHORIZED, ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        return build(ex.getErrorCode(), FORBIDDEN, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(ApiResponse.error(CommonErrorCode.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(FastApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleFastApi(FastApiException ex) {
        log.error("FastAPI 호출 실패: {}", ex.getMessage());
        return ResponseEntity.status(BAD_GATEWAY)
                .body(ApiResponse.error(CommonErrorCode.BAD_GATEWAY, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String aggregated = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation error");
        return build(CommonErrorCode.VALIDATION_ERROR, aggregated, UNPROCESSABLE_ENTITY, ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = "Missing required parameter: " + ex.getParameterName();
        return build(CommonErrorCode.BAD_REQUEST, message, BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
        String message = "Invalid value for parameter: " + ex.getName() + " (Expected: " + requiredType + ")";
        return build(CommonErrorCode.BAD_REQUEST, message, BAD_REQUEST, ex);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        log.debug("No static resource: {}", ex.getResourcePath());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<Void> handleAsyncTimeout(AsyncRequestTimeoutException ex) {
        // SSE 연결 타임아웃 - onTimeout 콜백에서 이미 정리되므로 무시
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return build(CommonErrorCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ApiResponse<Void>> build(BaseCode errorCode, HttpStatus status,
                                                       Exception ex) {
        logException(status, errorCode.getCode(), errorCode.getMessage(), ex);
        ApiResponse<Void> body = ApiResponse.error(errorCode, null);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ApiResponse<Void>> build(BaseCode errorCode,
                                                       String overrideMessage, HttpStatus status, Exception ex) {
        logException(status, errorCode.getCode(), overrideMessage, ex);
        ApiResponse<Void> body = ApiResponse.error(errorCode, overrideMessage);
        return ResponseEntity.status(status).body(body);
    }

    private void logException(HttpStatus status, String code, String message, Exception ex) {
        String logMsg = String.format("[%d] code=%s msg=%s", status.value(), code, message);

        if (status.is4xxClientError()) {
            log.warn(logMsg);
            return;
        }

        log.error(logMsg, ex);
    }
}
