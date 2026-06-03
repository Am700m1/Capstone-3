package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceRequestDTOOut {

    private Integer id;
    private Integer userId;
    private Integer apartmentId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
