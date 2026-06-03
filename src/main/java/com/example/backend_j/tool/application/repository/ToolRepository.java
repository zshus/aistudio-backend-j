package com.example.backend_j.tool.application.repository;

import com.example.backend_j.tool.application.domain.Tool;
import java.util.List;

public interface ToolRepository {
    Tool save(Tool tool);
    Tool findById(Long id);
    Tool findByToolId(String toolId);
    List<Tool> findAll();
    void deleteById(Long id);
}
