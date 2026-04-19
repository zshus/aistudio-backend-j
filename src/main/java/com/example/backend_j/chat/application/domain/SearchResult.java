package com.example.backend_j.chat.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private Long fileId;
    private String fileName;
    private Long folderId;
    private String chunkText;
    private Integer chunkIndex;
    private Double score;
}
