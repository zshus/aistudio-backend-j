package com.example.backend_j.vector.infrastructrue;

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
public class KeywordClientService {

    private final WebClient webClient;

    public KeywordClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(backendPUrl)
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<String> saveKeywords(Long fileId, Long folderId, String fileName, List<String> keywords) {
        try {
            Map<String, Object> body = Map.of(
                    "file_id", fileId,
                    "folder_id", folderId,
                    "file_name", fileName != null ? fileName : "",
                    "keywords", keywords
            );

            Map<String, Object> response = webClient.post()
                    .uri("/api/v1/keywords/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("keywords")) {
                return Collections.emptyList();
            }
            return (List<String>) response.get("keywords");

        } catch (Exception e) {
            log.error("키워드 저장 호출 실패: fileId={}, error={}", fileId, e.getMessage());
            throw new RuntimeException("키워드 저장 요청 실패: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getKeywords(Long fileId) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/api/v1/keywords/{fileId}", fileId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("keywords")) {
                return Collections.emptyList();
            }
            return (List<String>) response.get("keywords");
        } catch (Exception e) {
            log.warn("키워드 조회 실패: fileId={}, error={}", fileId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteKeywords(Long fileId) {
        try {
            webClient.delete()
                    .uri("/api/v1/keywords/{fileId}", fileId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("키워드 삭제 호출 실패: fileId={}, error={}", fileId, e.getMessage());
        }
    }

    public void updateEnabledByFolder(Long folderId, boolean enabled) {
        try {
            Map<String, Object> body = Map.of(
                    "folder_id", folderId,
                    "enabled", enabled
            );
            webClient.patch()
                    .uri("/api/v1/keywords/folder/enabled")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("폴더 enabled 업데이트 호출 실패: folderId={}, error={}", folderId, e.getMessage());
            throw new RuntimeException("폴더 enabled 업데이트 요청 실패: " + e.getMessage(), e);
        }
    }

    public void deleteKeywordsByFolder(Long folderId) {
        try {
            webClient.delete()
                    .uri("/api/v1/keywords/folder/{folderId}", folderId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("폴더 키워드 삭제 호출 실패: folderId={}, error={}", folderId, e.getMessage());
        }
    }

    public void updateEnabled(Long fileId, boolean enabled) {
        try {
            Map<String, Object> body = Map.of(
                    "file_id", fileId,
                    "enabled", enabled
            );
            webClient.patch()
                    .uri("/api/v1/keywords/enabled")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("enabled 업데이트 호출 실패: fileId={}, error={}", fileId, e.getMessage());
            throw new RuntimeException("enabled 업데이트 요청 실패: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractKeywords(Long fileId, Long folderId, String fileName) {
        try {
            Map<String, Object> body = Map.of(
                    "file_id", fileId,
                    "folder_id", folderId,
                    "file_name", fileName != null ? fileName : ""
            );

            Map<String, Object> response = webClient.post()
                    .uri("/api/v1/keywords/extract")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("keywords")) {
                return Collections.emptyList();
            }
            return (List<String>) response.get("keywords");

        } catch (Exception e) {
            log.error("키워드 추출 호출 실패: fileId={}, error={}", fileId, e.getMessage());
            throw new RuntimeException("키워드 추출 요청 실패: " + e.getMessage(), e);
        }
    }
}
