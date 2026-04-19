package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.application.domain.ChatRoom;
import com.example.backend_j.chat.application.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private final ChatRoomJpaRepository repository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return repository.save(chatRoom);
    }

    @Override
    public ChatRoom findById(Long roomId) {
        return repository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다. roomId=" + roomId));
    }

    @Override
    public List<ChatRoom> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long roomId) {
        repository.deleteById(roomId);
    }
}
