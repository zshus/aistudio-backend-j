package com.example.backend_j.tool.infrastructrue;

import com.example.backend_j.tool.application.domain.Tool;
import com.example.backend_j.tool.application.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ToolRepositoryImpl implements ToolRepository {

    private final ToolJpaRepository jpa;

    @Override
    public Tool save(Tool tool) {
        return jpa.save(tool);
    }

    @Override
    public Tool findById(Long id) {
        return jpa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tool not found: " + id));
    }

    @Override
    public Tool findByToolId(String toolId) {
        return jpa.findByToolId(toolId)
                .orElseThrow(() -> new IllegalArgumentException("tool not found: " + toolId));
    }

    @Override
    public List<Tool> findAll() {
        return jpa.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
