package com.example.backend_j.chat.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetHistoryCommand {
    private Long roomId;
}
