package com.example.backend_j.vector.application.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "folder")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "foldername", nullable = false, length = 100)
    private String folderName;

    
    @Column(name = "foldertype", nullable = false, length = 50)
    private String folderType;

    @Column(name = "useyn", nullable = false)
    private Boolean useYn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @Builder
    public Folder(String folderName,String folderType){
        this.folderName=folderName;
        this.folderType=folderType;
        this.useYn=true;
    }

    public void update(String folderName, String folderType, Boolean useYn) {
        this.folderName = folderName;
        this.folderType = folderType;
        this.useYn = useYn;
    }

    
}
