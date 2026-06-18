package com.service.alaw.platform.voice.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceFactCheckResponse;
import com.service.alaw.platform.voice.application.service.VoiceFactCheckQueryService;
import com.service.alaw.platform.voice.presentation.swagger.VoiceFactCheckSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoiceFactCheckController implements VoiceFactCheckSpec {

    private final VoiceFactCheckQueryService voiceFactCheckQueryService;

    @Override
    @GetMapping("/voice-records/{voiceRecordId}/fact-check")
    public ResponseEntity<ApiResponse<VoiceFactCheckResponse>> getFactCheck(
            @RequestParam Long contractId,
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {

        VoiceFactCheckResponse response = voiceFactCheckQueryService.getFactCheck(contractId, voiceRecordId, userId);
        return ApiResponse.retrieved(response);
    }
}
