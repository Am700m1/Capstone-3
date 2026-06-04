package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class OwnerReviewAnalysisDTOOut {

    private Integer ownerId;
    private String ownerName;
    private double averageRating;
    private int totalReviews;
    private String analysis;
}
