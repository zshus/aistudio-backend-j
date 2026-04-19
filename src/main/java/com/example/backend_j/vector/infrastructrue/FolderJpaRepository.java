package com.example.backend_j.vector.infrastructrue;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend_j.vector.application.domain.Folder;

public interface FolderJpaRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUseYn(Boolean useYn);
}
