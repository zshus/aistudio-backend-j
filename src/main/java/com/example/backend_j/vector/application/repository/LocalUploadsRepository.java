package com.example.backend_j.vector.application.repository;

import org.springframework.web.multipart.MultipartFile;
import com.example.backend_j.vector.application.domain.StoredFile;

public interface LocalUploadsRepository {
    StoredFile store(MultipartFile file);
    
} 
