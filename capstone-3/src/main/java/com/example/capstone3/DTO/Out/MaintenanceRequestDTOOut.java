package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.MaintenancePriority;
import com.example.capstone3.Enums.MaintenanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceRequestDTOOut {

    private Integer id;
    private Integer userId;
    private Integer apartmentId;
    private String title;
    private String description;
    private MaintenanceStatus status;
    private MaintenancePriority priority;
    private String aiCategory;
    private String aiSummary;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
