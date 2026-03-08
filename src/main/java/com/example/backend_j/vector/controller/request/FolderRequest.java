package com.example.backend_j.vector.controller.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FolderRequest {
    
    private Long folderId;
    
    private String folderName;    
    
    private String folderType;
    
    private Boolean useYn;


}
