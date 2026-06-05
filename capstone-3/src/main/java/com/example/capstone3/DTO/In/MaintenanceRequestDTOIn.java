package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceRequestDTOIn {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Apartment ID is required")
    private Integer apartmentId;

    @NotEmpty(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    private String description;
}
