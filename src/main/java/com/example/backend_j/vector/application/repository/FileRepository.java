package com.example.backend_j.vector.application.repository;


import java.util.List;
import com.example.backend_j.vector.application.domain.VdbFile;

public interface FileRepository {
    VdbFile save(VdbFile file);
    VdbFile finById(Long fileId);
    List<VdbFile> findByFolderId(Long folerId);
    void deleteById(Long fileId);
    void deleteByFolderId(Long folerId);
    
}
