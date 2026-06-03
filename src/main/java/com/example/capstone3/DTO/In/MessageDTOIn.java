package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTOIn {

    @NotNull(message = "Conversation ID is required")
    private Integer conversationId;

    @NotEmpty(message = "Message content is required")
    private String content;
}
