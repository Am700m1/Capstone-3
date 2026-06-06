package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTOOut {

    private Integer id;
    private Integer apartmentId;
    private Integer userId;
    private ReservationStatus status;
    private String message;
    private LocalDate reservationDate;
}
