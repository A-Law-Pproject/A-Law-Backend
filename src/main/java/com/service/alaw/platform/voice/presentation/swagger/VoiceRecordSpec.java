package com.service.alaw.platform.voice.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeStartResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceRecordSpec {

    @Operation(summary = "음성 녹음 삭제", description = "음성 녹음을 삭제합니다. 본인의 녹음만 삭제할 수 있으며 S3 파일도 함께 삭제됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "녹음을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> deleteVoiceRecord(
            @Parameter(description = "음성 녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId);

    @Operation(summary = "내 녹음 목록 조회", description = "로그인한 사용자의 모든 음성 녹음 목록을 최신순으로 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VoiceRecordResponse.class)))
    })
    ResponseEntity<ApiResponse<List<VoiceRecordResponse>>> getMyVoiceRecords(
            @Parameter(hidden = true) @CurrentUserId Long userId);

    @Operation(
            summary = "음성 녹음 저장",
            description = "음성 파일을 저장합니다. contractId 가 없으면 voice-only 레코드로 저장됩니다. "
                    + "지원 형식: MP3, WAV, MP4, WEBM, M4A"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "저장 성공",
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
            @Parameter(description = "계약서 ID (없으면 voice-only 모드)") @RequestParam(required = false) Long contractId,
            @Parameter(description = "녹음 제목 (선택)") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "음성 파일 (지원 형식: MP3, WAV, MP4, WEBM, M4A)", required = true,
                    content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("audio") MultipartFile file,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(
            summary = "음성 분석 시작",
            description = "저장된 음성 레코드의 분석을 시작합니다. "
                    + "계약서 연계 레코드는 RabbitMQ 비동기 팩트체크, "
                    + "voice-only 레코드는 FastAPI 동기 분석 결과를 즉시 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "분석 성공 또는 비동기 요청 시작"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "음성 녹음을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    ResponseEntity<ApiResponse<VoiceAnalyzeStartResponse>> analyzeVoiceRecord(
            @Parameter(description = "음성 녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );
}
