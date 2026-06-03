package com.example.backend_j.tool;

import com.example.backend_j.tool.application.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolStartupSyncer implements ApplicationRunner {

    private final ToolService toolService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            toolService.syncToOpenSearch();
        } catch (Exception e) {
            log.warn("startup tool sync 실패 (무시): {}", e.getMessage());
        }
    }
}
