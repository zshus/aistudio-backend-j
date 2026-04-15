package com.example.backend_j.vector.infrastructrue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Slf4j
@Service
public class VectorClientService {

    private final WebClient webClient;

    public VectorClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(backendPUrl)
                .build();
    }

    public void embed(Long fileId, String fileName, Long folderId, MultipartFile file) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file_id", fileId);
            builder.part("file_name", fileName);
            builder.part("folder_id", folderId);
            builder.part("file", new InputStreamResource(file.getInputStream()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
                @Override
                public long contentLength() {
                    return file.getSize();
                }
            }).contentType(MediaType.APPLICATION_OCTET_STREAM);

            webClient.post()
                    .uri("/api/v1/embed")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("backend-p embed 완료: fileId={}", fileId);
        } catch (IOException e) {
            log.error("파일 읽기 실패: fileId={}, error={}", fileId, e.getMessage());
            throw new RuntimeException("파일 읽기 실패: fileId=" + fileId, e);
        } catch (Exception e) {
            log.error("backend-p embed 호출 실패: fileId={}, error={}", fileId, e.getMessage());
            throw new RuntimeException("임베딩 요청 실패: fileId=" + fileId, e);
        }
    }

    public void deleteEmbed(Long fileId, Long folderId) {
        try {
            webClient.delete()
                    .uri("/api/v1/embed/{fileId}?folder_id={folderId}", fileId, folderId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("backend-p embed 삭제 완료: fileId={}", fileId);
        } catch (Exception e) {
            log.error("backend-p embed 삭제 호출 실패: fileId={}, error={}", fileId, e.getMessage());
        }
    }

    public void deleteCollection(Long folderId) {
        try {
            webClient.delete()
                    .uri("/api/v1/embed/collection/{folderId}", folderId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("backend-p 컬렉션 삭제 완료: folderId={}", folderId);
        } catch (Exception e) {
            log.error("backend-p 컬렉션 삭제 호출 실패: folderId={}, error={}", folderId, e.getMessage());
        }
    }
}
