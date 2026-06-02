package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTOIn {

    @NotNull(message = "Apartment ID is required")
    private Integer apartmentId;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Reservation date is required")
    @Future(message = "Reservation date must be in the future")
    private LocalDate reservationDate;

}
