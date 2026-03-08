package com.example.backend_j.vector.infrastructrue;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend_j.vector.application.domain.VdbFile;
import java.util.List;


public interface FileJpaRepository extends JpaRepository<VdbFile, Long>{
    List<VdbFile> findAllByFolderId(Long folderId);
    
}
