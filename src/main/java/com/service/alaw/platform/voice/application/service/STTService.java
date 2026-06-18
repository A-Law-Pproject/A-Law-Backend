package com.service.alaw.platform.voice.application.service;

import com.service.alaw.common.exception.ContractNotFoundException;
import com.service.alaw.common.exception.code.CommonErrorCode;
import com.service.alaw.infra.s3.S3UploadService;
import com.service.alaw.infra.stt.STTClient;
import com.service.alaw.platform.voice.application.dto.SttResponse;
import com.service.alaw.platform.voice.domain.entity.VoiceRecord;
import com.service.alaw.platform.voice.domain.repository.VoiceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class STTService {

    private final VoiceRecordRepository voiceRecordRepository;
    private final S3UploadService s3UploadService;
    private final STTClient sttClient;

    @Transactional
    public SttResponse transcribe(Long voiceRecordId, Long userId) {
        VoiceRecord voiceRecord = voiceRecordRepository.findById(voiceRecordId)
                .filter(v -> v.getUser().getUserId().equals(userId))
                .orElseThrow(() -> new ContractNotFoundException(CommonErrorCode.NOT_FOUND));

        log.info("[STTService] 변환 시작 - voiceRecordId={}, s3Key={}", voiceRecordId, voiceRecord.getS3Key());

        byte[] audioBytes = s3UploadService.download(voiceRecord.getS3Key());
        String filename = extractFilename(voiceRecord.getS3Key());
        String contentType = inferContentType(filename);

        String transcript = sttClient.transcribe(audioBytes, filename, contentType);

        voiceRecord.updateTranscript(transcript);
        voiceRecordRepository.save(voiceRecord);

        log.info("[STTService] 변환 완료 - voiceRecordId={}, 글자수={}", voiceRecordId, transcript.length());
        return new SttResponse(voiceRecordId, transcript);
    }

    private String extractFilename(String s3Key) {
        return s3Key.substring(s3Key.lastIndexOf('/') + 1);
    }

    private String inferContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".mp3"))  return "audio/mpeg";
        if (lower.endsWith(".wav"))  return "audio/wav";
        if (lower.endsWith(".mp4"))  return "audio/mp4";
        if (lower.endsWith(".webm")) return "audio/webm";
        if (lower.endsWith(".m4a"))  return "audio/x-m4a";
        return "audio/mpeg";
    }
}
