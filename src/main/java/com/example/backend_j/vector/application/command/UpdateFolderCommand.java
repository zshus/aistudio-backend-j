package com.example.backend_j.vector.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateFolderCommand {
    private Long folderId;
    private String folderName;    
    private String folderType;
    private Boolean useYn;

}
