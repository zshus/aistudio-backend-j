package com.example.backend_j.vector.infrastructrue;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.backend_j.vector.application.domain.VdbFile;
import com.example.backend_j.vector.application.repository.FileRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository{
    private final FileJpaRepository fileJpaRepository;

    @Override
    public VdbFile save(VdbFile file){
        return fileJpaRepository.save(file);
    }

    @Override
    public VdbFile finById(Long fileId){
        VdbFile find = fileJpaRepository.findById(fileId)
        .orElseThrow( () -> new IllegalArgumentException("file 존재하지 않습니다."));
        return find;
    }

    @Override
    public List<VdbFile> findByFolderId(Long folerId){
        return fileJpaRepository.findAllByFolderId(folerId);
    }

    @Override
    public void deleteById(Long fileId){
        fileJpaRepository.deleteById(fileId);
    }

    @Override
    public void deleteByFolderId(Long folerId){
        List<VdbFile> listitem = fileJpaRepository.findAllByFolderId(folerId);
        fileJpaRepository.deleteAll(listitem);
    }
    
}
