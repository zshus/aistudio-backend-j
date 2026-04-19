package com.example.backend_j.chat.controller.response;

import com.example.backend_j.chat.application.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse form(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .role(chatMessage.getRole())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static List<ChatMessageResponse> form(List<ChatMessage> list) {
        return list.stream().map(ChatMessageResponse::form).toList();
    }
}
