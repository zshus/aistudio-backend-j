package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.controller.response.ChatMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatClientService {

    private final WebClient webClient;

    public ChatClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(backendPUrl)
                .codecs(config -> config.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                .build();
    }

    public void streamQuery(
            String query,
            List<ChatMessageResponse> history,
            List<Long> folderIds,
            int topK,
            SseEmitter emitter
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("top_k", topK);
        if (folderIds != null && !folderIds.isEmpty()) {
            body.put("folder_ids", folderIds);
        }

        List<Map<String, String>> historyPayload = history.stream()
                .map(h -> Map.of("role", h.getRole(), "content", h.getContent()))
                .toList();
        body.put("conversation_history", historyPayload);

        try {
            webClient.post()
                    .uri("/api/v1/chat/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .doOnNext(line -> {
                        try {
                            emitter.send(SseEmitter.event().data(line, MediaType.APPLICATION_JSON));
                        } catch (Exception e) {
                            log.warn("SSE 전송 실패: {}", e.getMessage());
                        }
                    })
                    .doOnComplete(emitter::complete)
                    .doOnError(e -> {
                        log.error("Python SSE 스트림 오류: {}", e.getMessage());
                        emitter.completeWithError(e);
                    })
                    .blockLast();
        } catch (Exception e) {
            log.error("ChatClientService 오류: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }
}
