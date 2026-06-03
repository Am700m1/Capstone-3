package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDTOOut {

    private Integer id;
    private Integer userId;
    private Integer ownerId;
    private Integer apartmentId;
    private LocalDateTime createdAt;
    private List<MessageDTOOut> messages;
}
