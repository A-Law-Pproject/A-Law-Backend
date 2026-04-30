package com.service.alaw.platform.voice.presentation;

import com.service.alaw.infra.sse.SseEmitterManager;
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
public class VoiceRecordSseController {

    private final SseEmitterManager sseEmitterManager;

    /**
     * 음성 팩트체크 결과를 SSE로 구독한다.
     * analyze 응답의 jobId로 구독하면 voice_fact_check_result / voice_fact_check_complete 이벤트를 수신한다.
     */
    @GetMapping(value = "/analysis/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String jobId) {
        log.info("[VoiceSSE] 구독 요청 - jobId={}", jobId);
        return sseEmitterManager.register(jobId);
    }
}
