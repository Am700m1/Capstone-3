package com.example.capstone3.DTO.In;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractDTOIn {

    @NotNull(message = "Reservation ID is required")
    private Integer reservationId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Rent amount is required")
    @Positive(message = "Rent amount must be positive")
    private Double rentAmount;

    @Size(max = 500, message = "Contract file path must not exceed 500 characters")
    private String contractFilePath;

    // Status defaults to ACTIVE on creation not accepted from client
}
