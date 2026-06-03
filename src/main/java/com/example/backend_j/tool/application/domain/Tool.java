package com.example.backend_j.tool.application.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tool")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tool_id", unique = true, nullable = false, length = 100)
    private String toolId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "keywords", length = 1000)
    private String keywords;

    @Column(name = "use_yn", nullable = false)
    private Boolean useYn;

    @Column(name = "hidden", nullable = false)
    private Boolean hidden;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Tool(String toolId, String name, String description, String keywords, Boolean hidden) {
        this.toolId = toolId;
        this.name = name;
        this.description = description;
        this.keywords = keywords;
        this.useYn = true;
        this.hidden = hidden != null ? hidden : false;
    }

    public void update(String name, String description, String keywords, Boolean useYn, Boolean hidden) {
        this.name = name;
        this.description = description;
        this.keywords = keywords;
        this.useYn = useYn;
        this.hidden = hidden;
    }
}
