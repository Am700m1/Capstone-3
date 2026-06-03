package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTOOut {

    private Integer id;
    private Integer apartmentId;
    private Integer userId;
    private LocalDate reservationDate;
    private String status;           // PENDING CONFIRMED CANCELLED
    private String message;
}
