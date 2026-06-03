package com.example.backend_j.tool.infrastructrue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ToolClientService {

    private final WebClient webClient;

    public ToolClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(backendPUrl)
                .build();
    }

    public void upsert(String toolId, String name, String description, List<String> keywords, boolean useYn) {
        try {
            Map<String, Object> body = Map.of(
                    "tool_id", toolId,
                    "name", name,
                    "description", description != null ? description : "",
                    "keywords", keywords,
                    "use_yn", useYn
            );
            webClient.post()
                    .uri("/v1/tools/upsert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("tool upsert 호출 실패: toolId={}, error={}", toolId, e.getMessage());
            throw new RuntimeException("tool upsert 요청 실패: " + e.getMessage(), e);
        }
    }

    public void delete(String toolId) {
        try {
            webClient.delete()
                    .uri("/v1/tools/{toolId}", toolId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("tool 삭제 호출 실패: toolId={}, error={}", toolId, e.getMessage());
        }
    }

    public void syncAll(List<Map<String, Object>> tools) {
        try {
            webClient.post()
                    .uri("/v1/tools/sync")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("tools", tools))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("tool sync 완료: {}개", tools.size());
        } catch (Exception e) {
            log.error("tool sync 호출 실패: error={}", e.getMessage());
        }
    }
}
