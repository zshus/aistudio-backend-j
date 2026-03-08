package com.example.backend_j.vector.application.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StoredFile {
    private String originalFilename;  // 원본 파일명                                                                                                                                                              
    private String storedFilename;             // 저장된 파일명 (해시+확장자)                                                                                                                                              
    private String contentType;      // MIME 타입 (e.g. "image/png")                                                                                                                                             
    private Long size;                   // 파일 크기 (bytes)                                                                                                                                                        
    private String absolutePath;        // 절대 경로                                                                                                                                                                
    private String relativePath;        //uploads/ 기준 상대 경로

}
