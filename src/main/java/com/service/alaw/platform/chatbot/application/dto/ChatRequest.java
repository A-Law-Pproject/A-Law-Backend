package com.service.alaw.platform.chatbot.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ChatRequest(

        @NotBlank(message = "메시지를 입력해주세요.")
        @JsonProperty("message")
        String message,

        @JsonProperty("session_id")
        String sessionId
) {}
