package com.service.alaw.platform.voice.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceAnalyzeStartResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordResponse;
import java.util.List;
import com.service.alaw.platform.voice.application.service.VoiceRecordService;
import com.service.alaw.platform.voice.presentation.swagger.VoiceRecordSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoiceRecordController implements VoiceRecordSpec {

    private final VoiceRecordService voiceRecordService;

    private static final Set<String> ALLOWED_AUDIO_TYPES = Set.of(
            "audio/mpeg",
            "audio/wav",
            "audio/mp4",
            "audio/webm",
            "audio/x-m4a",
            "audio/m4a"
    );

    @Override
    @DeleteMapping("/voice-records/{voiceRecordId}")
    public ResponseEntity<ApiResponse<Void>> deleteVoiceRecord(
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {
        voiceRecordService.deleteVoiceRecord(voiceRecordId, userId);
        return ApiResponse.deleted();
    }

    @Override
    @GetMapping("/voice-records")
    public ResponseEntity<ApiResponse<List<VoiceRecordResponse>>> getMyVoiceRecords(
            @CurrentUserId Long userId) {
        return ApiResponse.retrieved(voiceRecordService.getMyVoiceRecords(userId));
    }

    /**
     * 음성 분석 트리거.
     * - 계약서 연계 레코드: RabbitMQ 비동기 팩트체크 → data=null 로 200 반환
     * - voice-only 레코드: FastAPI 동기 분석 → 200 OK + VoiceAnalysisResponse 반환
     */
    @Override
    @PostMapping("/voice-records/{voiceRecordId}/analyze")
    public ResponseEntity<ApiResponse<VoiceAnalyzeStartResponse>> analyzeVoiceRecord(
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {

        VoiceAnalyzeStartResponse result = voiceRecordService.analyze(voiceRecordId, userId);
        return ApiResponse.success(result);
    }

    /**
     * 음성 파일 업로드.
     * contractId 가 null 이면 voice-only 레코드로 저장한다.
     */
    @Override
    @PostMapping(value = "/voice-records", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VoiceRecordResponse>> saveVoiceRecord(
            @RequestParam(required = false) Long contractId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("audio") MultipartFile file,
            @CurrentUserId Long userId) {

        validateAudioFile(file);

        VoiceRecordResponse response = voiceRecordService.save(contractId, userId, title, file);
        return ApiResponse.created(response);
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
