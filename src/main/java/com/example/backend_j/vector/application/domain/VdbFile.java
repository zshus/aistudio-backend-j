package com.example.backend_j.vector.application.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vdbfile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VdbFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folderid")
    private Long folderId;

    @Column(name = "filename", nullable = false, length = 100)
    private String fileName;

    @Column(name = "filepath", nullable = false, length = 255)
    private String filePath;

    @Column(name = "useyn", nullable = false)
    private Boolean useYn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public VdbFile(Long folderId, String fileName, String filePath) {
        this.folderId= folderId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.useYn = true;
    }

    public void update(Boolean useYn) {
        this.useYn = useYn;
    }

    public void update(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
