package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class BuildingDTOOut {

    private Integer id;
    private Integer ownerId;
    private String ownerName;
    private String name;
    private String district;
    private String address;
    private Double latitude;
    private Double longitude;
}
