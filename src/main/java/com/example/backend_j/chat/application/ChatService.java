package com.example.backend_j.chat.application;

import com.example.backend_j.chat.application.command.*;
import com.example.backend_j.chat.application.domain.ChatMessage;
import com.example.backend_j.chat.application.domain.ChatRoom;
import com.example.backend_j.chat.application.repository.ChatMessageRepository;
import com.example.backend_j.chat.application.repository.ChatRoomRepository;
import com.example.backend_j.chat.controller.response.ChatMessageResponse;
import com.example.backend_j.chat.controller.response.ChatRoomResponse;
import com.example.backend_j.chat.infrastructrue.ChatClientService;
import com.example.backend_j.vector.application.domain.Folder;
import com.example.backend_j.vector.application.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int DEFAULT_TOP_K = 5;
    private static final int DEFAULT_CONTEXT_SIZE = 10;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatClientService chatClientService;
    private final FolderRepository folderRepository;

    @Transactional
    public ChatRoomResponse createRoom(CreateRoomCommand command) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(command.getRoomName())
                .build();
        chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.form(chatRoom);
    }

    public List<ChatRoomResponse> getRoomList() {
        return ChatRoomResponse.form(chatRoomRepository.findAll());
    }

    @Transactional
    public ChatRoomResponse deleteRoom(DeleteRoomCommand command) {
        ChatRoom chatRoom = chatRoomRepository.findById(command.getRoomId());
        chatMessageRepository.deleteByRoomId(chatRoom.getId());
        chatRoomRepository.deleteById(chatRoom.getId());
        return ChatRoomResponse.form(chatRoom);
    }

    public List<ChatMessageResponse> getHistory(GetHistoryCommand command) {
        return ChatMessageResponse.form(chatMessageRepository.findByRoomId(command.getRoomId()));
    }

    public SseEmitter sendMessage(SendMessageCommand command) {
        // 1. 채팅방 존재 확인
        chatRoomRepository.findById(command.getRoomId());

        // 2. 검색 대상 폴더 결정
        //    - folderIds 지정 → 해당 폴더 중 useYn=true만
        //    - folderIds 미지정(null/빈 리스트) → useYn=true 전체 폴더
        List<Folder> targetFolders = resolveTargetFolders(command.getFolderIds());
        if (targetFolders.isEmpty()) {
            throw new IllegalArgumentException("검색 가능한 활성화된 폴더가 없습니다.");
        }

        // 3. 사용자 메시지 저장
        ChatMessage userMessage = ChatMessage.builder()
                .roomId(command.getRoomId())
                .role("user")
                .content(command.getMessage())
                .build();
        chatMessageRepository.save(userMessage);

        // 4. 대화 맥락 로드 (동기 - JPA 세션 문제 방지)
        int contextSize = command.getContextSize() != null ? command.getContextSize() : DEFAULT_CONTEXT_SIZE;
        List<ChatMessageResponse> contextHistory =
                ChatMessageResponse.form(chatMessageRepository.findRecentByRoomId(command.getRoomId(), contextSize));

        // 5. SSE Emitter 생성 (타임아웃 120초)
        SseEmitter emitter = new SseEmitter(120_000L);

        // 6. 비동기 스트리밍 시작
        int topK = command.getTopK() != null ? command.getTopK() : DEFAULT_TOP_K;
        CompletableFuture.runAsync(() ->
                streamResponse(emitter, command, targetFolders, topK, contextHistory));

        return emitter;
    }

    /**
     * folderIds 지정 → 해당 폴더만 (단, useYn=true인 것만)
     * folderIds 미지정 → useYn=true 전체 폴더
     */
    private List<Folder> resolveTargetFolders(List<Long> folderIds) {
        if (folderIds == null || folderIds.isEmpty()) {
            return folderRepository.findAllByUseYn(true);
        }
        return folderIds.stream()
                .map(folderRepository::findById)
                .filter(Folder::getUseYn)
                .collect(Collectors.toList());
    }

    private void streamResponse(SseEmitter emitter, SendMessageCommand command,
                                List<Folder> targetFolders, int topK,
                                List<ChatMessageResponse> contextHistory) {
        List<Long> folderIdList = targetFolders.stream()
                .map(Folder::getId)
                .collect(Collectors.toList());

        ChatClientService.StreamResult result = chatClientService.streamQuery(
                command.getMessage(),
                contextHistory,
                folderIdList,
                topK,
                emitter
        );

        saveAssistantMessage(command.getRoomId(), result.content().trim(), result.tool());
    }

    @Transactional
    public void saveAssistantMessage(Long roomId, String content, String tool) {
        ChatMessage assistantMessage = ChatMessage.builder()
                .roomId(roomId)
                .role("assistant")
                .content(content)
                .tool(tool)
                .build();
        chatMessageRepository.save(assistantMessage);
    }
}
