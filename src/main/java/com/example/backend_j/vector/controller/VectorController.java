package com.example.backend_j.vector.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_j.vector.application.VectorService;
import com.example.backend_j.vector.application.command.CreateFileCommand;
import com.example.backend_j.vector.application.command.CreateFolderCommand;
import com.example.backend_j.vector.application.command.RetriveFileCommand;
import com.example.backend_j.vector.application.command.UpdateFileCommand;
import com.example.backend_j.vector.application.command.UpdateFolderCommand;
import com.example.backend_j.vector.controller.request.FileRequest;
import com.example.backend_j.vector.controller.request.FolderRequest;
import com.example.backend_j.vector.controller.response.FileResponse;
import com.example.backend_j.vector.controller.response.FolderResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vector")
public class VectorController {
    private final VectorService service;

    //folder
    @PostMapping("/folder/create")
    public ResponseEntity<FolderResponse> createFolder(@RequestBody FolderRequest request) {
        CreateFolderCommand createFolderCommand=CreateFolderCommand.builder()
        .folderName(request.getFolderName())
        .folderType(request.getFolderType())
        .build();

        FolderResponse folderResponse=service.createFolder(createFolderCommand);        
        return ResponseEntity.ok(folderResponse);
    }

    @PostMapping("/folder/update")
    public ResponseEntity<FolderResponse> updateFolder(@RequestBody FolderRequest request) {
        UpdateFolderCommand updateFolderCommand=UpdateFolderCommand.builder()
        .folderId(request.getFolderId())
        .folderName(request.getFolderName())
        .folderType(request.getFolderType())
        .useYn(request.getUseYn()).build();

        FolderResponse folderResponse=service.updateFolder(updateFolderCommand);        
        return ResponseEntity.ok(folderResponse);
    }

    @PostMapping("/folder/delete")
    public ResponseEntity<FolderResponse> postMethodName(@RequestBody FolderRequest request) {
        UpdateFolderCommand updateFolderCommand=UpdateFolderCommand.builder()
        .folderId(request.getFolderId())
        .folderName(request.getFolderName())
        .folderType(request.getFolderType())
        .useYn(request.getUseYn())
        .build();
        
         FolderResponse folderResponse=service.deleteFolder(updateFolderCommand);        
        return ResponseEntity.ok(folderResponse);
    }

    @GetMapping("/folder/list")
    public ResponseEntity<List<FolderResponse>> getAllList() {
        List<FolderResponse> list=service.getAllFolderList();
        return ResponseEntity.ok(list);
    }

    //file
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file, @RequestParam Long folderId) {
        CreateFileCommand createFileCommand=CreateFileCommand.builder()
        .folderId(folderId)
        .build();

        FileResponse fileResponse= service.uploadFile(file, createFileCommand);       
        return ResponseEntity.ok(fileResponse);
    }

    @PostMapping(value = "/file/overwrite", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> overwriteFile(@RequestParam("file") MultipartFile file, @RequestParam Long fileId) {
        UpdateFileCommand updateFileCommand=UpdateFileCommand.builder()
        .fileId(fileId)
        .build();

        FileResponse fileResponse= service.overwriteFile(file, updateFileCommand);       
        return ResponseEntity.ok(fileResponse);
    }

    @PostMapping("/file/update")
    public ResponseEntity<FileResponse> updateFile(@RequestBody FileRequest request) {
        UpdateFileCommand updateFileCommand=UpdateFileCommand.builder()
        .fileId(request.getFileId())
        .fileName(request.getFileName())
        .folderId(request.getFolderId())
        .useYn(request.getUseYn())
        .build();

        FileResponse fileResponse= service.updateFile(updateFileCommand);        
        return ResponseEntity.ok(fileResponse);
    }

    @PostMapping("/file/delete")
    public ResponseEntity<FileResponse> deleteFile(@RequestBody FileRequest request) {
        UpdateFileCommand updateFileCommand=UpdateFileCommand.builder()
        .fileId(request.getFileId())
        .fileName(request.getFileName())
        .folderId(request.getFolderId())
        .useYn(request.getUseYn())
        .build();
        
        FileResponse fileResponse= service.deleteFile(updateFileCommand);        
        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("/file/list")
    public ResponseEntity<List<FileResponse>> getFileAllList(@RequestParam Long folderId) {
        RetriveFileCommand retriveFileCommand=RetriveFileCommand.builder()
        .folderId(folderId)
        .build();

        List<FileResponse> list = service.getAllFileList(retriveFileCommand);
        return ResponseEntity.ok(list);
    }
    
    
    
    
    
   
    
    
    
    
    
}
