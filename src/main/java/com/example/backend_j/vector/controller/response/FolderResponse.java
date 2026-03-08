package com.example.backend_j.vector.controller.response;

import java.util.List;

import com.example.backend_j.vector.application.domain.Folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class FolderResponse {
    private Long folderId;
   
    private String folderName;    
    
    private String folderType;
    
    private Boolean useYn;

    public static FolderResponse form(Folder folder){
        return FolderResponse.builder()
        .folderId(folder.getId())
        .folderName(folder.getFolderName())
        .folderType(folder.getFolderType())
        .useYn(folder.getUseYn())
        .build();
    }

    public static List<FolderResponse> form(List<Folder> folderList){
        return folderList.stream().map(FolderResponse::form).toList();
    }
    
}
