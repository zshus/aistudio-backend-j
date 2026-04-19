package com.example.backend_j.chat.controller;

import com.example.backend_j.chat.application.ChatService;
import com.example.backend_j.chat.application.command.*;
import com.example.backend_j.chat.controller.request.ChatMessageRequest;
import com.example.backend_j.chat.controller.request.ChatRoomRequest;
import com.example.backend_j.chat.controller.response.ChatMessageResponse;
import com.example.backend_j.chat.controller.response.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService service;

    // 채팅방 생성
    @PostMapping("/room/create")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest request) {
        CreateRoomCommand command = CreateRoomCommand.builder()
                .roomName(request.getRoomName())
                .build();
        return ResponseEntity.ok(service.createRoom(command));
    }

    // 채팅방 목록
    @GetMapping("/room/list")
    public ResponseEntity<List<ChatRoomResponse>> getRoomList() {
        return ResponseEntity.ok(service.getRoomList());
    }

    // 채팅방 삭제 (메시지 포함 cascade)
    @PostMapping("/room/delete")
    public ResponseEntity<ChatRoomResponse> deleteRoom(@RequestBody ChatRoomRequest request) {
        DeleteRoomCommand command = DeleteRoomCommand.builder()
                .roomId(request.getRoomId())
                .build();
        return ResponseEntity.ok(service.deleteRoom(command));
    }

    // 채팅 히스토리 조회
    @GetMapping("/message/list")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(@RequestParam Long roomId) {
        GetHistoryCommand command = GetHistoryCommand.builder()
                .roomId(roomId)
                .build();
        return ResponseEntity.ok(service.getHistory(command));
    }

    // 메시지 전송 + SSE 스트리밍 응답
    @PostMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(@RequestBody ChatMessageRequest request) {
        SendMessageCommand command = SendMessageCommand.builder()
                .roomId(request.getRoomId())
                .folderIds(request.getFolderIds())
                .message(request.getMessage())
                .topK(request.getTopK())
                .contextSize(request.getContextSize())
                .build();
        return service.sendMessage(command);
    }
}
