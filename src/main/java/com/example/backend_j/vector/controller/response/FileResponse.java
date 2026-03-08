package com.example.backend_j.vector.controller.response;

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

    public static FileResponse form(VdbFile file){
        return FileResponse.builder()
        .fileId(file.getId())
        .fileName(file.getFileName())
        .folderId(file.getFolderId())
        .useYn(file.getUseYn())
        .build();
    }

    public static List<FileResponse> form(List<VdbFile> fileList){
        return fileList.stream()
        .map(FileResponse::form)
        .toList();
    }

}
