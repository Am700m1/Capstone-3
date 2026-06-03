package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTOOut {

    private Integer id;
    private Integer userId;
    private Integer apartmentId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
