package com.example.backend_j.tool.controller;

import com.example.backend_j.tool.application.ToolService;
import com.example.backend_j.tool.controller.request.ToolRequest;
import com.example.backend_j.tool.controller.response.ToolResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tools")
public class ToolController {

    private final ToolService toolService;

    @GetMapping("/list")
    public ResponseEntity<List<ToolResponse>> list() {
        return ResponseEntity.ok(toolService.getAllTools());
    }

    @PostMapping("/create")
    public ResponseEntity<ToolResponse> create(@RequestBody ToolRequest req) {
        return ResponseEntity.ok(toolService.createTool(req));
    }

    @PostMapping("/update")
    public ResponseEntity<ToolResponse> update(@RequestBody ToolRequest req) {
        return ResponseEntity.ok(toolService.updateTool(req));
    }

    @PostMapping("/delete")
    public ResponseEntity<ToolResponse> delete(@RequestBody ToolRequest req) {
        return ResponseEntity.ok(toolService.deleteTool(req.getId()));
    }
}
