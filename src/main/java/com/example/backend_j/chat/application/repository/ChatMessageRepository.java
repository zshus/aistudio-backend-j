package com.example.backend_j.chat.application.repository;

import java.util.List;

import com.example.backend_j.chat.application.domain.ChatMessage;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findByRoomId(Long roomId);
    List<ChatMessage> findRecentByRoomId(Long roomId, int limit);
    void deleteByRoomId(Long roomId);
}
