package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserPreferenceDTOIn {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Work latitude is required")
    private Double workLatitude;

    @NotNull(message = "Work longitude is required")
    private Double workLongitude;

    @NotNull(message = "Max budget is required")
    @Positive(message = "Max budget must be a positive number")
    private Double maxBudget;

    @Positive(message = "Max commute minutes must be positive")
    private Integer maxCommuteMinutes;

    private Boolean requiresParking;
    private Boolean requiresElevator;
    private Boolean requiresFurnished;

    private String gymPreference;
    private String cafesPreference;
    private String hospitalPreference;
    private String schoolPreference;
    private String publicTransportPreference;

    private Integer preferredBedrooms;
    private Integer preferredBathrooms;

    @Size(max = 100, message = "Preferred district must not exceed 100 characters")
    private String preferredDistrict;

    @NotNull(message = "lookingForRoommate status is required")
    private Boolean lookingForRoommate;

    @Positive(message = "Roommate budget must be a positive number")
    private Double roommateBudget;

}
