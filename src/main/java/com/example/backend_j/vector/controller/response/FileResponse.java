package com.example.backend_j.vector.controller.response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.backend_j.vector.application.domain.VdbFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class FileResponse {

    private Long fileId;

    private Long folderId;

    private String fileName;

    private Boolean useYn;

    private List<String> keywords;

    public static FileResponse form(VdbFile file) {
        List<String> kwList = (file.getKeywords() != null && !file.getKeywords().isBlank())
                ? Arrays.asList(file.getKeywords().split(","))
                : Collections.emptyList();
        return FileResponse.builder()
                .fileId(file.getId())
                .fileName(file.getFileName())
                .folderId(file.getFolderId())
                .useYn(file.getUseYn())
                .keywords(kwList)
                .build();
    }

    public static List<FileResponse> form(List<VdbFile> fileList) {
        return fileList.stream()
                .map(FileResponse::form)
                .toList();
    }
}
