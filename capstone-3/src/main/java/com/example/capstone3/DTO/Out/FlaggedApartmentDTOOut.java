package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class FlaggedApartmentDTOOut {

    private Integer id;
    private String apartmentNumber;
    private String district;
    private Double monthlyRent;
    private Integer totalReservations;
    private Integer totalCancellations;
    private Double cancellationRate;
    private Double averageRate;
}
