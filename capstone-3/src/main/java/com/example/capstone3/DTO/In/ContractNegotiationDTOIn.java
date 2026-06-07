package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContractNegotiationDTOIn {

    @Positive(message = "Requested rent must be positive")
    private Double requestedRent;

    @Positive(message = "Counter-offer rent must be positive")
    private Double counterOfferRent;

    @Size(max = 1000, message = "Negotiation message must not exceed 1000 characters")
    private String negotiationMessage;
}
