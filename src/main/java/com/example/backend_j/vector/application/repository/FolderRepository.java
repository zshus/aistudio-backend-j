package com.example.backend_j.vector.application.repository;

import java.util.List;

import com.example.backend_j.vector.application.domain.Folder;

public interface FolderRepository {
    Folder save(Folder folder);
    Folder findById(Long folderId);
    List<Folder> findAll();
    void deleteById(Long folderId);
    
}
