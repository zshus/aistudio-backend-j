package com.example.backend_j.tool.controller.response;

import com.example.backend_j.tool.application.domain.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ToolResponse {
    private Long id;
    private String toolId;
    private String name;
    private String description;
    private List<String> keywords;
    private Boolean useYn;
    private Boolean hidden;

    public static ToolResponse from(Tool tool) {
        List<String> kw = (tool.getKeywords() == null || tool.getKeywords().isBlank())
                ? List.of()
                : Arrays.stream(tool.getKeywords().split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).toList();
        return ToolResponse.builder()
                .id(tool.getId())
                .toolId(tool.getToolId())
                .name(tool.getName())
                .description(tool.getDescription())
                .keywords(kw)
                .useYn(tool.getUseYn())
                .hidden(tool.getHidden())
                .build();
    }
}
