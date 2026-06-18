package com.service.alaw.platform.voice.application.dto;

public record SttResponse(
        Long voiceRecordId,
        String transcript
) {}
