package com.example.backend_j.tool.controller.request;

import lombok.Getter;
import java.util.List;

@Getter
public class ToolRequest {
    private Long id;
    private String toolId;
    private String name;
    private String description;
    private List<String> keywords;
    private Boolean useYn;
    private Boolean hidden;
}
