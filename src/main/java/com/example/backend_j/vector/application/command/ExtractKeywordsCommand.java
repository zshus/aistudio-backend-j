package com.example.backend_j.vector.application.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExtractKeywordsCommand {
    private Long fileId;
    private Long folderId;
    private String fileName;
}
