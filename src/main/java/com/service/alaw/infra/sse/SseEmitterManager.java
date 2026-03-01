package com.service.alaw.infra.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class SseEmitterManager {

    private final Map<String, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    private static final Long DEFAULT_TIMEOUT = 300_000L; // 5분

    /**
     * SSE 구독 등록
     */
    public SseEmitter register(String s3Key) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        subscribers.putIfAbsent(s3Key, new CopyOnWriteArrayList<>());
        subscribers.get(s3Key).add(emitter);

        log.info("[SSE] New subscription for s3Key={}", s3Key);

        // 연결 확인 이벤트 전송
        sendToEmitter(emitter, "connection", Map.of("status", "connected", "s3Key", s3Key));

        // 완료/타임아웃 시 자동 제거
        emitter.onCompletion(() -> remove(s3Key, emitter));
        emitter.onTimeout(() -> remove(s3Key, emitter));
        emitter.onError(e -> remove(s3Key, emitter));

        return emitter;
    }

    /**
     * 특정 s3Key에 이벤트 전송
     */
    public void send(String s3Key, String eventType, Object data) {
        CopyOnWriteArrayList<SseEmitter> emitters = subscribers.get(s3Key);

        if (emitters == null || emitters.isEmpty()) {
            log.warn("[SSE] No subscribers for s3Key={}", s3Key);
            return;
        }

        log.info("[SSE] Sending {} event to {} subscribers for s3Key={}",
                eventType, emitters.size(), s3Key);

        emitters.forEach(emitter -> sendToEmitter(emitter, eventType, data));
    }

    /**
     * 개별 Emitter에 이벤트 전송
     */
    private void sendToEmitter(SseEmitter emitter, String eventType, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(data));
        } catch (IOException e) {
            log.error("[SSE] Error sending event: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    /**
     * 구독 제거
     */
    private void remove(String s3Key, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = subscribers.get(s3Key);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                subscribers.remove(s3Key);
            }
        }
        log.info("[SSE] Removed subscription for s3Key={}", s3Key);
    }
}
