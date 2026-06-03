package com.example.backend_j.tool.infrastructrue;

import com.example.backend_j.tool.application.domain.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ToolJpaRepository extends JpaRepository<Tool, Long> {
    Optional<Tool> findByToolId(String toolId);
}
