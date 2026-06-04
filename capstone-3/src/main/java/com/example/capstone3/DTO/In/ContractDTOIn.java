package com.example.capstone3.DTO.In;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractDTOIn {

    @NotNull(message = "Contract number is required")
    private String contractNumber;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    private Double monthlyRent;

    @Positive(message = "Security deposit must be positive")
    private Double securityDeposit;

    private Boolean signed;
    private LocalDate signedDate;

    @Size(max = 500, message = "PDF path must not exceed 500 characters")
    private String pdfPath;

    // Status defaults to ACTIVE on creation not accepted from client
}
