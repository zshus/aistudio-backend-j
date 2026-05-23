package com.example.backend_j.vector.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FileRequest {

    private Long fileId;

    private Long folderId;

    private String fileName;

    private Boolean useYn;

    private List<String> keywords;

}
