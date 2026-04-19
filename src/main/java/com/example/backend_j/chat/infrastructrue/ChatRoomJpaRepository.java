package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.application.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
}
