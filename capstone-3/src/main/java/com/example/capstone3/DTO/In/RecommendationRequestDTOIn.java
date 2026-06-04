package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecommendationRequestDTOIn {

    @NotNull(message = "User ID is required")
    private Integer userId;

    // Search radius for nearby amenities in metres. Default: 3000
    private int radiusMetres = 3000;
}
