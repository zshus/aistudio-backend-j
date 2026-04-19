package com.example.backend_j.chat.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomRequest {
    private Long roomId;
    private String roomName;
}
