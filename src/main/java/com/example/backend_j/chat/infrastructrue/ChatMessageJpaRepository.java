package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.application.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByCreatedAtAsc(Long roomId);
    // 최신순으로 limit개 조회 (Pageable로 동적 limit)
    List<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    void deleteAllByRoomId(Long roomId);
}
