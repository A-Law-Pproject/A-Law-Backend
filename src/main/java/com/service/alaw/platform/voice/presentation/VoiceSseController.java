package com.service.alaw.platform.voice.presentation;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.voice.presentation.swagger.VoiceSseSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/v1/voice-records")
@RequiredArgsConstructor
public class VoiceSseController implements VoiceSseSpec {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping(value = "/analysis/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String jobId) {
        log.info("[VoiceSSE] 구독 요청 - jobId={}", jobId);
        return sseEmitterManager.register(jobId);
    }
}
