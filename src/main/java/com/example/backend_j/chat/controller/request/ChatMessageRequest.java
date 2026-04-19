package com.example.backend_j.chat.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {
    private Long roomId;
    private List<Long> folderIds; // null 또는 빈 리스트 → useYn=true 전체 폴더 검색
    private String message;
    private Integer topK;         // 폴더당 Milvus 검색 결과 개수 (default: 5)
    private Integer contextSize;  // 대화 맥락 메시지 개수 (default: 10)
}
