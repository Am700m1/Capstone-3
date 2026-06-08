package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ContractDTOIn {

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    private Double monthlyRent;

    @Positive(message = "Security deposit must be positive")
    private Double securityDeposit;

    // Status defaults to ACTIVE on creation not accepted from client
}
