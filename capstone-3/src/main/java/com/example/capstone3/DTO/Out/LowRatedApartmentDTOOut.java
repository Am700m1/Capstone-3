package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class LowRatedApartmentDTOOut {

    private Integer id;
    private String apartmentNumber;
    private String district;
    private Double monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double averageRating;
    private String aiSummary;
}
