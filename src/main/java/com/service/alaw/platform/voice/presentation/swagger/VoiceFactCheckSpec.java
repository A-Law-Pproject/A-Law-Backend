package com.service.alaw.platform.voice.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceFactCheckSpec {

    @Operation(
            summary = "음성 분석 결과 조회",
            description = "음성 녹음의 전체 분석 결과를 조회합니다. "
                    + "계약서 연계 레코드는 mode=FACT_CHECK(factCheck 필드), "
                    + "voice-only 레코드는 mode=VOICE_ONLY(voiceAnalysis 필드)로 반환됩니다. "
                    + "분석이 아직 완료되지 않은 경우 status만 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VoiceRecordDetailResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "음성 녹음을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<VoiceRecordDetailResponse>> getVoiceDetail(
            @Parameter(description = "음성 녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );
}
