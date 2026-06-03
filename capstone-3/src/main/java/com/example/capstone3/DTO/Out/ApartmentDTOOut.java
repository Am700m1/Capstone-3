package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ApartmentDTOOut {

    private Integer id;
    private Integer buildingId;
    private Integer ownerId;
    private String district;
    private String title;
    private String description;
    private Double monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;
    private Integer floorNumber;
    private String status;
    private Boolean furnished;
    private Boolean available;
    private LocalDate availableFrom;
    private String allowedTenantType;
    private Boolean waterIncluded;
    private Boolean internetIncluded;
    private Boolean electricityIncluded;
    private List<ApartmentImageDTOOut> images;
}
