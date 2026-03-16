package com.service.alaw.platform.contract.presentation;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.contract.presentation.swagger.ContractSseSpec;
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
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractSseController implements ContractSseSpec {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping(value = "/analysis/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String jobId) {
        log.info("[SSE] 구독 요청 - jobId={}", jobId);
        return sseEmitterManager.register(jobId);
    }
}
