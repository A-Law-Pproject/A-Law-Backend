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

@Tag(name = "Contract OCR", description = "계약서 OCR 처리 API")
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
}
