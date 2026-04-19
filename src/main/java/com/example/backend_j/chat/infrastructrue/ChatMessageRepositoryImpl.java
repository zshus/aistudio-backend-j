package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.application.domain.ChatMessage;
import com.example.backend_j.chat.application.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository repository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return repository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findByRoomId(Long roomId) {
        return repository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Override
    public List<ChatMessage> findRecentByRoomId(Long roomId, int limit) {
        // 최신순으로 limit개 조회 후 시간 순서로 역정렬 (오래된 것 → 최신 순)
        List<ChatMessage> recent = repository.findByRoomIdOrderByCreatedAtDesc(
                roomId, PageRequest.of(0, limit));
        Collections.reverse(recent);
        return recent;
    }

    @Override
    @Transactional
    public void deleteByRoomId(Long roomId) {
        repository.deleteAllByRoomId(roomId);
    }
}
