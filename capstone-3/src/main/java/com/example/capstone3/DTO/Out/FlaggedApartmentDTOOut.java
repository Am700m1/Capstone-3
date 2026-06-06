package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class FlaggedApartmentDTOOut {

    private Integer id;
    private String title;
    private String district;
    private Double monthlyRent;
    private Integer totalReservations;
    private Integer totalCancellations;
    private Double cancellationRate;      // e.g. 0.60 = 60%
    private Double averageRate;           // average across all owner's apartments
}
