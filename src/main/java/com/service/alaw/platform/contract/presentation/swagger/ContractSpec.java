package com.service.alaw.platform.contract.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.contract.application.dto.ocr.FastApiOcrResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Contract", description = "계약서 OCR 및 분석 API")
public interface ContractSpec {

    @Operation(
            summary = "계약서 이미지 OCR 처리",
            description = "계약서 이미지를 받아 OCR 처리 후 텍스트 오버레이용 좌표 정보를 반환합니다. 지원 형식: JPG, PNG, GIF, WEBP, PDF"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OCR 처리 성공",
                    content = @Content(schema = @Schema(implementation = FastApiOcrResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파일이 비어있거나 지원하지 않는 형식)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<FastApiOcrResponse>> processOcr(
            @Parameter(
                    description = "계약서 이미지 파일 (지원 형식: JPG, PNG, GIF, WEBP, PDF)",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")

            )
            @RequestParam("file") MultipartFile imageFile,
            @Parameter(hidden = true)
            @CurrentUserId Long userId
    );

    @Operation(
            summary = "계약서 분석 결과 SSE 구독",
            description = "s3Key를 기반으로 계약서 AI 분석 결과를 실시간으로 수신합니다. (Server-Sent Events)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 연결 성공"
            )
    })
    SseEmitter subscribe(
            @Parameter(description = "S3 저장 키 (OCR 응답에서 반환된 값)", required = true)
            @RequestParam("s3Key") String s3Key
    );
}
