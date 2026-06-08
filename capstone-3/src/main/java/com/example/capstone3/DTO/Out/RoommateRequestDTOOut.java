package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.RoommateStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RoommateRequestDTOOut {

    private Integer id;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private RoommateStatus status;
    private LocalDate createdAt;
    private String direction;
}
