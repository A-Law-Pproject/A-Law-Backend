package com.service.alaw.infra.messaging;

import com.service.alaw.infra.sse.SseEmitterManager;
import com.service.alaw.platform.voice.application.dto.VoiceFactCheckResultMessage;
import com.service.alaw.platform.voice.domain.document.VoiceFactCheckDocument;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.repository.VoiceFactCheckRepository;
import com.service.alaw.platform.voice.domain.repository.VoiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceFactCheckConsumer {

    private final VoiceRecordRepository voiceRecordRepository;
    private final VoiceFactCheckRepository voiceFactCheckRepository;
    private final SseEmitterManager sseEmitterManager;

    @RabbitListener(queues = "${app.rabbitmq.voice-result-queue}")
    public void handleFactCheckResult(VoiceFactCheckResultMessage message) {
        String jobId = message.jobId();
        log.info("[VoiceConsumer] 팩트체크 결과 수신 - voiceRecordId={}, jobId={}, status={}",
                message.voiceRecordId(), jobId, message.status());

        VoiceRecord voiceRecord = voiceRecordRepository.findByJobId(jobId)
                .orElse(null);

        if (voiceRecord == null) {
            log.warn("[VoiceConsumer] VoiceRecord 없음 - jobId={}", jobId);
            return;
        }

        try {
            if (message.isSuccess()) {
                VoiceFactCheckDocument saved = voiceFactCheckRepository.save(VoiceFactCheckDocument.from(message));
                voiceRecord.complete();
                voiceRecord.linkAnalysis(saved.getId());
                log.info("[VoiceConsumer] MongoDB 저장 완료 - voiceRecordId={}", message.voiceRecordId());

                sseEmitterManager.send(jobId, "voice_fact_check_result", Map.of(
                        "voiceRecordId", message.voiceRecordId(),
                        "transcript", message.transcript() != null ? message.transcript() : "",
                        "factCheckItems", message.factCheckItems() != null ? message.factCheckItems() : List.of()
                ));
            } else {
                voiceRecord.fail();
                log.warn("[VoiceConsumer] 팩트체크 실패 - jobId={}, error={}", jobId, message.errorMessage());
                sseEmitterManager.send(jobId, "error", Map.of("message", "팩트체크에 실패했습니다."));
            }
        } catch (Exception e) {
            voiceRecord.fail();
            log.error("[VoiceConsumer] 처리 중 예외 발생 - jobId={}", jobId, e);
            sseEmitterManager.send(jobId, "error", Map.of("message", "결과 처리 중 서버 오류가 발생했습니다."));
        } finally {
            voiceRecordRepository.save(voiceRecord);
        }

        sseEmitterManager.send(jobId, "voice_fact_check_complete", Map.of(
                "status", message.status() != null ? message.status() : "FAILED",
                "jobId", jobId,
                "voiceRecordId", message.voiceRecordId()
        ));
    }
}
