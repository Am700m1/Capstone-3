package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.util.List;

@Data
public class ApartmentDTOOut {

    private Integer id;
    private Integer buildingId;
    private String district;        // From building — useful for filtering
    private String title;
    private String description;
    private Double monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;
    private String status;
    private List<ApartmentImageDTOOut> images;
}
