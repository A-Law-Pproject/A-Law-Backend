package com.service.alaw.platform.chatbot.application.service;

import com.service.alaw.infra.ai.AIClient;
import com.service.alaw.platform.chatbot.application.dto.ChatRequest;
import com.service.alaw.platform.chatbot.application.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final AIClient aiClient;

    public ChatResponse chat(ChatRequest request) {
        log.info("챗봇 서비스 요청 - messageLength: {}", request.message() != null ? request.message().length() : 0);
        return aiClient.chat(request);
    }
}
