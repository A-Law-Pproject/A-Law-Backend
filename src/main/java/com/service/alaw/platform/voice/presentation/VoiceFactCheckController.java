package com.service.alaw.platform.voice.presentation;

import com.service.alaw.common.aop.CurrentUserId;
import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.voice.application.dto.VoiceRecordDetailResponse;
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
    @GetMapping("/voice-records/{voiceRecordId}/analysis")
    public ResponseEntity<ApiResponse<VoiceRecordDetailResponse>> getVoiceDetail(
            @PathVariable Long voiceRecordId,
            @CurrentUserId Long userId) {

        VoiceRecordDetailResponse response = voiceFactCheckQueryService.getVoiceDetail(voiceRecordId, userId);
        return ApiResponse.retrieved(response);
    }
}
