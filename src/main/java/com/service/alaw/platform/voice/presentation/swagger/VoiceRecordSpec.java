package com.service.alaw.platform.voice.presentation.swagger;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.SttResponse;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
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

import java.util.List;

@Tag(name = "Voice Record", description = "음성 녹음 API")
public interface VoiceRecordSpec {

    @Operation(summary = "음성 녹음 저장 (계약서 연결)", description = "기존 계약서에 연결하여 음성을 저장합니다. 동일 계약서에 녹음이 있으면 덮어씁니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = VoiceRecordResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 형식",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "계약서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecord(
            @Parameter(description = "계약서 ID", required = true) @PathVariable Long contractId,
            @Parameter(description = "녹음 제목 (선택)") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "음성 파일 (MP3/WAV/MP4/WEBM/M4A)", required = true) @RequestParam("audio") MultipartFile file,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "음성 녹음 저장 (계약서 연결 없음)", description = "계약서를 선택하지 않고 음성을 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = VoiceRecordResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 파일 형식",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecordWithoutContract(
            @Parameter(description = "녹음 제목 (선택)") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "음성 파일 (MP3/WAV/MP4/WEBM/M4A)", required = true) @RequestParam("audio") MultipartFile file,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "전체 녹음 목록 조회", description = "사용자의 모든 녹음 파일을 최신순으로 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<ApiResponse<List<VoiceRecordResponse>>> getVoiceRecords(
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "특정 계약서의 녹음 조회", description = "특정 계약서에 연결된 녹음 파일을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VoiceRecordResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "녹음을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<VoiceRecordResponse>> getVoiceRecordByContract(
            @Parameter(description = "계약서 ID", required = true) @PathVariable Long contractId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "녹음 파일 삭제", description = "녹음 파일을 삭제합니다. S3 파일도 함께 삭제됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "녹음을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> deleteVoiceRecord(
            @Parameter(description = "녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "팩트체크 분석 시작", description = "저장된 음성의 팩트체크 분석을 시작합니다. 계약서가 미연결 상태면 contractId 필수입니다. 응답의 jobId로 SSE를 구독하세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "분석 요청 성공",
                    content = @Content(schema = @Schema(implementation = VoiceAnalyzeResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "녹음 또는 계약서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<VoiceAnalyzeResponse>> analyzeVoiceRecord(
            @Parameter(description = "녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(description = "계약서 ID (계약서 미연결 시 필수)") @RequestParam(value = "contractId", required = false) Long contractId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );

    @Operation(summary = "음성 텍스트 변환 (STT)", description = "저장된 음성 파일을 텍스트로 변환합니다. OpenAI Whisper API를 사용합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변환 성공",
                    content = @Content(schema = @Schema(implementation = SttResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "녹음을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "STT 변환 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<SttResponse>> transcribeVoiceRecord(
            @Parameter(description = "녹음 ID", required = true) @PathVariable Long voiceRecordId,
            @Parameter(hidden = true) @CurrentUserId Long userId
    );
}
