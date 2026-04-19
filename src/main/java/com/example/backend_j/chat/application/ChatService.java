package com.example.backend_j.chat.application;

import com.example.backend_j.chat.application.command.*;
import com.example.backend_j.chat.application.domain.ChatMessage;
import com.example.backend_j.chat.application.domain.ChatRoom;
import com.example.backend_j.chat.application.domain.SearchResult;
import com.example.backend_j.chat.application.repository.ChatMessageRepository;
import com.example.backend_j.chat.application.repository.ChatRoomRepository;
import com.example.backend_j.chat.controller.response.ChatMessageResponse;
import com.example.backend_j.chat.controller.response.ChatRoomResponse;
import com.example.backend_j.chat.infrastructrue.SearchClientService;
import com.example.backend_j.vector.application.domain.Folder;
import com.example.backend_j.vector.application.domain.VdbFile;
import com.example.backend_j.vector.application.repository.FileRepository;
import com.example.backend_j.vector.application.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    private final SearchClientService searchClientService;
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

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
        StringBuilder assistantContent = new StringBuilder();
        try {
            // 7. 활성 폴더 id 리스트 → Backend-P 단일 호출 → score 내림차순으로 이미 정렬된 결과
            // contextHistory는 LLM 추가 시 프롬프트 구성에 활용 (현재는 서버 내부 보관)
            List<Long> folderIdList = targetFolders.stream()
                    .map(Folder::getId)
                    .collect(Collectors.toList());

            List<SearchResult> allResults = searchClientService
                    .search(command.getMessage(), folderIdList, topK);

            // 9. 파일 useYn=true 필터링
            List<SearchResult> filtered = allResults.stream()
                    .filter(r -> isFileActive(r.getFileId()))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                emitter.send(SseEmitter.event()
                        .name("result")
                        .data("{\"message\":\"관련 문서를 찾을 수 없습니다.\"}", MediaType.APPLICATION_JSON));
                assistantContent.append("관련 문서를 찾을 수 없습니다.");
            } else {
                // 10. 검색 결과 SSE 스트리밍 (score 높은 순)
                for (SearchResult result : filtered) {
                    emitter.send(SseEmitter.event()
                            .name("result")
                            .data(result, MediaType.APPLICATION_JSON));
                    assistantContent
                            .append("[").append(result.getFileName()).append("] ")
                            .append(result.getChunkText())
                            .append("\n\n");
                }
            }

            // 11. assistant 메시지 DB 저장
            saveAssistantMessage(command.getRoomId(), assistantContent.toString().trim());

            // 12. [done 이벤트]
            emitter.send(SseEmitter.event()
                    .name("done")
                    .data("{\"totalResults\":" + filtered.size()
                            + ",\"searchedFolders\":" + targetFolders.size() + "}",
                            MediaType.APPLICATION_JSON));
            emitter.complete();

        } catch (Exception e) {
            log.error("SSE 스트리밍 오류: roomId={}, error={}", command.getRoomId(), e.getMessage());
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"message\":\"" + e.getMessage() + "\"}", MediaType.APPLICATION_JSON));
            } catch (Exception ignored) {}
            emitter.completeWithError(e);
        }
    }

    @Transactional
    public void saveAssistantMessage(Long roomId, String content) {
        ChatMessage assistantMessage = ChatMessage.builder()
                .roomId(roomId)
                .role("assistant")
                .content(content)
                .build();
        chatMessageRepository.save(assistantMessage);
    }

    private boolean isFileActive(Long fileId) {
        try {
            VdbFile file = fileRepository.finById(fileId);
            return Boolean.TRUE.equals(file.getUseYn());
        } catch (Exception e) {
            log.warn("파일 활성화 여부 확인 실패: fileId={}", fileId);
            return false;
        }
    }
}
