package com.service.alaw.platform.voice.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceRecordSpec {

    @Operation(
            summary = "음성 녹음 저장",
            description = "계약서에 음성 메모를 저장하고 팩트체크를 요청합니다. 기존 녹음이 있으면 덮어씁니다. 지원 형식: MP3, WAV, MP4, WEBM, M4A"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "저장 성공 (status=PENDING, 팩트체크 처리 대기 중)",
                    content = @Content(schema = @Schema(implementation = VoiceRecordResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파일이 비어있거나 지원하지 않는 형식)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "계약서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecord(
            @Parameter(description = "계약서 ID", required = true) @RequestParam Long contractId,
            @Parameter(description = "녹음 제목 (선택)") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "음성 파일 (지원 형식: MP3, WAV, MP4, WEBM, M4A)", required = true,
                    content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("audio") MultipartFile file,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );
}
