package com.example.backend_j.vector.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.backend_j.vector.application.command.CreateFileCommand;
import com.example.backend_j.vector.application.command.CreateFolderCommand;
import com.example.backend_j.vector.application.command.RetriveFileCommand;
import com.example.backend_j.vector.application.command.UpdateFileCommand;
import com.example.backend_j.vector.application.command.UpdateFolderCommand;
import com.example.backend_j.vector.application.domain.Folder;
import com.example.backend_j.vector.application.domain.StoredFile;
import com.example.backend_j.vector.application.domain.VdbFile;
import com.example.backend_j.vector.application.repository.FileRepository;
import com.example.backend_j.vector.application.repository.FolderRepository;
import com.example.backend_j.vector.application.repository.LocalUploadsRepository;
import com.example.backend_j.vector.controller.response.FileResponse;
import com.example.backend_j.vector.controller.response.FolderResponse;
import com.example.backend_j.vector.infrastructrue.VectorClientService;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final LocalUploadsRepository localUploadsRepository;
    private final VectorClientService vectorClientService;

    @Transactional
    public FolderResponse createFolder(CreateFolderCommand command){
        Folder folder =Folder.builder()
        .folderName(command.getFolderName())
        .folderType(command.getFolderType())
        .build();

        folderRepository.save(folder);

        return FolderResponse.form(folder);
    }

    @Transactional
    public FolderResponse updateFolder(UpdateFolderCommand command){
        Folder folder=folderRepository.findById(command.getFolderId());
        folder.update(command.getFolderName(), command.getFolderType(), command.getUseYn());
        folderRepository.save(folder);

        return FolderResponse.form(folder);
    }

    @Transactional
    public FolderResponse deleteFolder(UpdateFolderCommand command){
        Folder folder=folderRepository.findById(command.getFolderId());
        folderRepository.deleteById(folder.getId());
        fileRepository.deleteByFolderId(folder.getId());

        vectorClientService.deleteCollection(folder.getId());

        return FolderResponse.form(folder);
    }

    
    public List<FolderResponse> getAllFolderList(){
        List<Folder> list=folderRepository.findAll();
        return FolderResponse.form(list);
     }

    @Transactional
    public FileResponse uploadFile(MultipartFile file, CreateFileCommand command){
        StoredFile storedFile=localUploadsRepository.store(file);
        VdbFile dVdbFile=VdbFile.builder()
        .folderId(command.getFolderId())
        .fileName(storedFile.getOriginalFilename())
        .filePath(storedFile.getRelativePath())
        .build();
        fileRepository.save(dVdbFile);

        try {
            vectorClientService.embed(dVdbFile.getId(), dVdbFile.getFileName(), dVdbFile.getFolderId(), file);
        } catch (Exception e) {
            localUploadsRepository.delete(storedFile.getRelativePath());
            throw e; // @Transactional이 DB 롤백 처리
        }

        return FileResponse.form(dVdbFile);
    }

    @Transactional
    public FileResponse overwriteFile(MultipartFile file, UpdateFileCommand command){
        StoredFile storedFile=localUploadsRepository.store(file);

        VdbFile find=fileRepository.finById(command.getFileId());
        String oldFilePath=find.getFilePath();
        find.update(storedFile.getOriginalFilename(),storedFile.getRelativePath());
        fileRepository.save(find);

        try {
            vectorClientService.deleteEmbed(find.getId(), find.getFolderId());
            vectorClientService.embed(find.getId(), find.getFileName(), find.getFolderId(), file);
        } catch (Exception e) {
            localUploadsRepository.delete(storedFile.getRelativePath());
            throw e; // @Transactional이 DB 롤백 처리 (이전 파일 정보로 복원)
        }

        localUploadsRepository.delete(oldFilePath);
        return FileResponse.form(find);
    }

    @Transactional
    public FileResponse updateFile(UpdateFileCommand command){
        VdbFile find=fileRepository.finById(command.getFileId());
        find.update(command.getUseYn());
        fileRepository.save(find);

        return FileResponse.form(find);
    }

    @Transactional
    public FileResponse deleteFile(UpdateFileCommand command){
        VdbFile find=fileRepository.finById(command.getFileId());
        fileRepository.deleteById(command.getFileId());

        vectorClientService.deleteEmbed(find.getId(), find.getFolderId());

        return FileResponse.form(find);
    }

    public List<FileResponse> getAllFileList(RetriveFileCommand command){
        List<VdbFile> list=fileRepository.findByFolderId(command.getFolderId());
        return FileResponse.form(list);
    }

    
}
