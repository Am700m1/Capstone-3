package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTOOut {

    private Integer id;
    private Integer conversationId;
    private Integer senderId;
    private String senderRole;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
}
