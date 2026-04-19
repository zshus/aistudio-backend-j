package com.example.backend_j.vector.infrastructrue;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.backend_j.vector.application.domain.Folder;
import com.example.backend_j.vector.application.repository.FolderRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepository{

    private final FolderJpaRepository repository;

    @Override
    public Folder save(Folder folder){
        return repository.save(folder);
    }

    @Override
    public Folder findById(Long folderId){
        Folder find = repository.findById(folderId)
        .orElseThrow(() -> new IllegalArgumentException("폴더가 존재하지 않습니다."));
        return find;
    }

    @Override
    public List<Folder> findAll(){
        return repository.findAll();
    }

    @Override
    public List<Folder> findAllByUseYn(Boolean useYn) {
        return repository.findAllByUseYn(useYn);
    }

    @Override
    public void deleteById(Long folderId){
        repository.deleteById(folderId);
    }


}
