package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.PreferenceLevel;
import lombok.Data;

@Data
public class UserPreferenceDTOOut {

    private Integer id;
    private Integer userId;
    private Double workLatitude;
    private Double workLongitude;
    private String workplaceName;
    private Double maxBudget;
    private Boolean requiresParking;
    private Boolean requiresElevator;
    private Boolean requiresFurnished;
    private PreferenceLevel gymPreference;
    private PreferenceLevel cafesPreference;
    private PreferenceLevel hospitalPreference;
    private PreferenceLevel schoolPreference;
    private PreferenceLevel publicTransportPreference;
    private Integer preferredBedrooms;
    private Integer preferredBathrooms;
    private String preferredDistrict;
}
