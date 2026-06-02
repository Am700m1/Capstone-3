package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPreferenceDTOIn {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Work latitude is required")
    private Double workLatitude;

    @NotNull(message = "Work longitude is required")
    private Double workLongitude;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be a positive number")
    private Double budget;

    @NotEmpty(message = "Marital status is required")
    @Size(max = 10, message = "Marital status must not exceed 10 characters")
    // single or married only
    private String maritalStatus;

    @NotNull(message = "Children count is required")
    @Min(value = 0, message = "Children count cannot be negative")
    private Integer childrenCount;

    // optional
    private Integer preferredBedrooms;
    private Integer preferredBathrooms;

    @Size(max = 100, message = "Preferred district must not exceed 100 characters")
    private String preferredDistrict;
}
