package com.service.alaw.platform.chatbot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "chat")
public class ChatDocument {
    @Id
    private String id;          // MongoDB ObjectId

    private Long contractId;    // PostgreSQL Contract PK (어떤 방인지 구분)
    private List<ChatMessage> messages; // 대화 기록 배열
}
