package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class UserPreferenceDTOOut {

    private Integer id;
    private Integer userId;
    private Double workLatitude;
    private Double workLongitude;
    private Double maxBudget;
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
    private String preferredDistrict;
}
