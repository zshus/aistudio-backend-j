package com.example.backend_j.chat.application.repository;

import java.util.List;

import com.example.backend_j.chat.application.domain.ChatRoom;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    ChatRoom findById(Long roomId);
    List<ChatRoom> findAll();
    void deleteById(Long roomId);
}
