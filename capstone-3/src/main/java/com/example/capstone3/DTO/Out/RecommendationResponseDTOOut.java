package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.util.List;

@Data
public class RecommendationResponseDTOOut {

    private List<RankedApartmentDTOOut> rankedApartments;
    private String recommendation;
}
