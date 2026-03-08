package com.example.backend_j.vector.infrastructrue;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend_j.vector.application.domain.Folder;
public interface FolderJpaRepository extends JpaRepository<Folder, Long> {
}
