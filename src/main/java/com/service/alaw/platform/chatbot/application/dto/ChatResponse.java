package com.service.alaw.platform.chatbot.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatResponse(

        @JsonProperty("answer")
        String answer,

        @JsonProperty("session_id")
        String sessionId,

        @JsonProperty("sources")
        List<String> sources,

        @JsonProperty("turn_count")
        int turnCount
) {}
