package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkplaceDTOIn {

    @NotBlank(message = "Workplace name is required")
    @Size(max = 200, message = "Workplace name must not exceed 200 characters")
    private String workplaceName;
}
