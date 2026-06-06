package com.example.capstone3.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoommateMatchDTOOut {
    private Integer candidateId;
    private String candidateName; // We will map this manually for safety
    private Integer matchPercentage;
    private String reason;
}