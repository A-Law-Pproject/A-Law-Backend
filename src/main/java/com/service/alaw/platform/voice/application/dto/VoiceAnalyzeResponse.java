package com.service.alaw.platform.voice.application.dto;

public record VoiceAnalyzeResponse(
        Long voiceRecordId,
        String jobId
) {}
