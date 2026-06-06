package com.example.capstone3.DTO.In;

import com.example.capstone3.Enums.MessageSenderType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTOIn {

    private Integer conversationId;

    private Integer userId;
    private Integer ownerId;
    private Integer apartmentId;

    @NotNull(message = "Sender type is required")
    private MessageSenderType senderType;

    @NotNull(message = "Sender ID is required")
    private Integer senderId;

    @NotEmpty(message = "Message content is required")
    private String content;
}
