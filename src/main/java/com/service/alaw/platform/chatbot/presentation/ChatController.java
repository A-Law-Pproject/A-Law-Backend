package com.service.alaw.platform.chatbot.presentation;

import com.service.alaw.common.response.ApiResponse;
import com.service.alaw.platform.chatbot.application.dto.ChatRequest;
import com.service.alaw.platform.chatbot.application.dto.ChatResponse;
import com.service.alaw.platform.chatbot.application.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request);
        return ApiResponse.success(response);
    }
}
