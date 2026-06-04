package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class UnderpricedApartmentDTOOut {

    private Integer id;
    private String title;
    private String district;
    private Double monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;

    private Integer totalReservations;       // High demand
    private Double avgDaysToReserve;         // Fast reservations
    private Double averageRating;            // Good reviews

    private Double score;                    // Final weighted score
}
