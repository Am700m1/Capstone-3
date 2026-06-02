package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class UserPreferenceDTOOut {

    private Integer id;
    private Integer userId;
    private Double workLatitude;
    private Double workLongitude;
    private Double budget;
    private String maritalStatus;
    private Integer childrenCount;
    private Integer preferredBedrooms;
    private Integer preferredBathrooms;
    private String preferredDistrict;
}
