package com.example.backend_j.chat.infrastructrue;

import com.example.backend_j.chat.application.domain.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchClientService {

    private final WebClient webClient;

    public SearchClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(backendPUrl)
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<SearchResult> search(String query, List<Long> folderIds, int topK) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "query", query,
                    "folder_ids", folderIds,
                    "top_k", topK
            );

            Map<String, Object> response = webClient.post()
                    .uri("/api/v1/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("results")) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null) {
                return Collections.emptyList();
            }

            return results.stream()
                    .map(r -> SearchResult.builder()
                            .fileId(toLong(r.get("file_id")))
                            .fileName((String) r.get("file_name"))
                            .folderId(toLong(r.get("folder_id")))
                            .chunkText((String) r.get("chunk_text"))
                            .chunkIndex(toInt(r.get("chunk_index")))
                            .score(toDouble(r.get("score")))
                            .build())
                    .toList();

        } catch (Exception e) {
            log.error("backend-p 검색 호출 실패: folderIds={}, error={}", folderIds, e.getMessage());
            throw new RuntimeException("검색 요청 실패: " + e.getMessage(), e);
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        return ((Number) value).longValue();
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        return ((Number) value).intValue();
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        return ((Number) value).doubleValue();
    }
}
