package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class NeighborhoodSummaryDTOOut {

    private String district;
    private Integer radiusMetres;
    private NearbyCounts nearbyCounts;
    private String summary;

    @Data
    public static class NearbyCounts {
        private int schools;
        private int supermarkets;
        private int restaurants;
        private int hospitals;
    }
}
