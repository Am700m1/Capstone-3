package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BuildingDTOIn {

    @NotNull(message = "Owner ID is required")
    private Integer ownerId;

    @NotEmpty(message = "Building name is required")
    @Size(max = 150, message = "Building name must not exceed 150 characters")
    private String name;

    @NotEmpty(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotEmpty(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;
}
