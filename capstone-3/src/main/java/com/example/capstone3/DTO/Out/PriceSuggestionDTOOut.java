package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class PriceSuggestionDTOOut {

    private Integer apartmentId;
    private String suggestedPrice;
    private String explanation;
}
