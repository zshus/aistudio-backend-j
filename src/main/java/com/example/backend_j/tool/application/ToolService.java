package com.example.backend_j.tool.application;

import com.example.backend_j.tool.application.domain.Tool;
import com.example.backend_j.tool.application.repository.ToolRepository;
import com.example.backend_j.tool.controller.request.ToolRequest;
import com.example.backend_j.tool.controller.response.ToolResponse;
import com.example.backend_j.tool.infrastructrue.ToolClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToolService {

    private final ToolRepository toolRepository;
    private final ToolClientService toolClientService;

    @Transactional
    public ToolResponse createTool(ToolRequest req) {
        Tool tool = Tool.builder()
                .toolId(req.getToolId())
                .name(req.getName())
                .description(req.getDescription())
                .keywords(req.getKeywords() != null ? String.join(",", req.getKeywords()) : "")
                .hidden(req.getHidden() != null ? req.getHidden() : false)
                .build();
        toolRepository.save(tool);
        toolClientService.upsert(tool.getToolId(), tool.getName(), tool.getDescription(),
                parseKeywords(tool.getKeywords()), tool.getUseYn());
        return ToolResponse.from(tool);
    }

    @Transactional
    public ToolResponse updateTool(ToolRequest req) {
        Tool tool = toolRepository.findById(req.getId());
        tool.update(req.getName(), req.getDescription(),
                req.getKeywords() != null ? String.join(",", req.getKeywords()) : "",
                req.getUseYn(), req.getHidden() != null ? req.getHidden() : false);
        toolRepository.save(tool);
        toolClientService.upsert(tool.getToolId(), tool.getName(), tool.getDescription(),
                parseKeywords(tool.getKeywords()), tool.getUseYn());
        return ToolResponse.from(tool);
    }

    @Transactional
    public ToolResponse deleteTool(Long id) {
        Tool tool = toolRepository.findById(id);
        toolRepository.deleteById(id);
        toolClientService.delete(tool.getToolId());
        return ToolResponse.from(tool);
    }

    public List<ToolResponse> getAllTools() {
        return toolRepository.findAll().stream()
                .map(ToolResponse::from)
                .collect(Collectors.toList());
    }

    public void syncToOpenSearch() {
        List<Tool> tools = toolRepository.findAll();
        if (tools.isEmpty()) return;
        List<Map<String, Object>> payload = tools.stream()
                .map(t -> Map.<String, Object>of(
                        "tool_id", t.getToolId(),
                        "name", t.getName(),
                        "description", t.getDescription() != null ? t.getDescription() : "",
                        "keywords", parseKeywords(t.getKeywords()),
                        "use_yn", t.getUseYn(),
                        "hidden", t.getHidden()
                ))
                .collect(Collectors.toList());
        toolClientService.syncAll(payload);
    }

    private List<String> parseKeywords(String keywords) {
        if (keywords == null || keywords.isBlank()) return List.of();
        return Arrays.stream(keywords.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
