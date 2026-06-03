package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class BuildingDTOOut {

    private Integer id;
    private Integer ownerId;
    private String ownerName;
    private String name;
    private String city;
    private String district;
    private String street;
    private Double latitude;
    private Double longitude;
    private Integer constructionYear;
    private Integer totalFloors;
    private Boolean hasElevator;
    private Boolean hasSecurity;
    private Boolean hasParking;
    private Boolean petsAllowed;
}
