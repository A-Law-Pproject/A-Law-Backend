package com.service.alaw.platform.chatbot.domain;

import com.service.alaw.platform.BaseTimeDocument;

public class ChatMessage extends BaseTimeDocument {
    private String sender;      // USER or AI
    private String content;     // 메시지 내용
}
