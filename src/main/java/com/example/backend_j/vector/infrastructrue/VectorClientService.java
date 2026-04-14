package com.example.backend_j.vector.infrastructrue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class VectorClientService {

    private final RestClient restClient;

    public VectorClientService(@Value("${backend.p.url}") String backendPUrl) {
        this.restClient = RestClient.builder()
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

            

            restClient.post()
                    .uri("/api/v1/embed")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .retrieve()
                    .toBodilessEntity();

            log.info("backend-p embed 완료: fileId={}", fileId);
        } catch (IOException e) {
            log.error("파일 읽기 실패: fileId={}, error={}", fileId, e.getMessage());
        } catch (Exception e) {
            log.error("backend-p embed 호출 실패: fileId={}, error={}", fileId, e.getMessage());
        }
    }

    public void deleteEmbed(Long fileId) {
        try {
            restClient.delete()
                    .uri("/api/v1/embed/{fileId}", fileId)
                    .retrieve()
                    .toBodilessEntity();

            log.info("backend-p embed 삭제 완료: fileId={}", fileId);
        } catch (Exception e) {
            log.error("backend-p embed 삭제 호출 실패: fileId={}, error={}", fileId, e.getMessage());
        }
    }
}
