package com.service.alaw.platform.voice.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.SttResponse;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import com.service.alaw.platform.voice.application.service.STTService;
import com.service.alaw.platform.voice.application.service.VoiceRecordService;
import com.service.alaw.platform.voice.presentation.swagger.VoiceRecordSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class VoiceRecordController implements VoiceRecordSpec {

    private final VoiceRecordService voiceRecordService;
    private final STTService sttService;

    private static final Set<String> ALLOWED_AUDIO_TYPES = Set.of(
            "audio/mpeg",
            "audio/wav",
            "audio/mp4",
            "audio/webm",
            "audio/x-m4a",
            "audio/m4a"
    );

    // POST /api/v1/contracts/{contractId}/voice-records — 계약서에 바로 연결하여 저장
    @Override
    @PostMapping(value = "/api/v1/contracts/{contractId}/voice-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecord(
            @PathVariable Long contractId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("audio") MultipartFile file,
            @CurrentUserId Long userId) {

        validateAudioFile(file);
        VoiceRecordResponse response = voiceRecordService.save(contractId, userId, title, file);
        return ApiResponse.created(response);
    }

    // POST /api/v1/voice-records — 계약서 연결 없이 저장
    @Override
    @PostMapping(value = "/api/v1/voice-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecordWithoutContract(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("audio") MultipartFile file,
            @CurrentUserId Long userId) {

        validateAudioFile(file);
        VoiceRecordResponse response = voiceRecordService.saveWithoutContract(userId, title, file);
        return ApiResponse.created(response);
    }

    // GET /api/v1/voice-records — 전체 녹음 목록 조회
    @Override
    @GetMapping("/api/v1/voice-records")
    public ResponseEntity<ApiResponse<List<VoiceRecordResponse>>> getVoiceRecords(
            @CurrentUserId Long userId) {

        return ApiResponse.retrieved(voiceRecordService.findAll(userId));
    }

    // GET /api/v1/contracts/{contractId}/voice-record — 특정 계약서의 녹음 조회
    @Override
    @GetMapping("/api/v1/contracts/{contractId}/voice-record")
    public ResponseEntity<ApiResponse<VoiceRecordResponse>> getVoiceRecordByContract(
            @PathVariable Long contractId,
            @CurrentUserId Long userId) {

        return ApiResponse.retrieved(voiceRecordService.findByContract(contractId, userId));
    }

    // DELETE /api/v1/voice-records/{voiceRecordId} — 녹음 삭제
    @Override
    @DeleteMapping("/api/v1/voice-records/{voiceRecordId}")
    public ResponseEntity<ApiResponse<Void>> deleteVoiceRecord(
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {

        voiceRecordService.delete(voiceRecordId, userId);
        return ApiResponse.deleted();
    }

    // POST /api/v1/voice-records/{voiceRecordId}/analyze — 분석 시작
    @Override
    @PostMapping("/api/v1/voice-records/{voiceRecordId}/analyze")
    public ResponseEntity<ApiResponse<VoiceAnalyzeResponse>> analyzeVoiceRecord(
            @PathVariable Long voiceRecordId,
            @RequestParam(value = "contractId", required = false) Long contractId,
            @CurrentUserId Long userId) {

        VoiceAnalyzeResponse response = voiceRecordService.analyze(voiceRecordId, userId, contractId);
        return ApiResponse.success(response);
    }

    // POST /api/v1/voice-records/{voiceRecordId}/transcribe — STT 변환
    @Override
    @PostMapping("/api/v1/voice-records/{voiceRecordId}/transcribe")
    public ResponseEntity<ApiResponse<SttResponse>> transcribeVoiceRecord(
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {

        SttResponse response = sttService.transcribe(voiceRecordId, userId);
        return ApiResponse.success(response);
    }

    private void validateAudioFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        if (!ALLOWED_AUDIO_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원 형식: MP3, WAV, MP4, WEBM, M4A)");
        }
    }
}
