package com.example.backend_j.chat.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SendMessageCommand {
    private Long roomId;
    private List<Long> folderIds; // 비어있으면 useYn=true 전체 폴더 검색
    private String message;
    private Integer topK;
    private Integer contextSize;
}
