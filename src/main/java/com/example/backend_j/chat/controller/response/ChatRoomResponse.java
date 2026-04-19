package com.example.backend_j.chat.controller.response;

import com.example.backend_j.chat.application.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String roomName;
    private LocalDateTime createdAt;

    public static ChatRoomResponse form(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getRoomName())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static List<ChatRoomResponse> form(List<ChatRoom> list) {
        return list.stream().map(ChatRoomResponse::form).toList();
    }
}
