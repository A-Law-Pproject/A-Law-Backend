package com.service.alaw.platform.voice.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceFactCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceFactCheckSpec {

    @Operation(
            summary = "팩트체크 결과 조회",
            description = "음성 녹음의 팩트체크 결과를 조회합니다. status가 COMPLETED일 때 결과를 확인할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VoiceFactCheckResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "음성 녹음 또는 팩트체크 결과를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<VoiceFactCheckResponse>> getFactCheck(
            @Parameter(description = "계약서 ID", required = true) @RequestParam Long contractId,
            @Parameter(description = "음성 녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );
}
