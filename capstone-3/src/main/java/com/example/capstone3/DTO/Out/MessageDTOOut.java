package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.MessageSenderType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTOOut {

    private Integer id;
    private Integer conversationId;
    private MessageSenderType senderType;
    private Integer senderId;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
}
