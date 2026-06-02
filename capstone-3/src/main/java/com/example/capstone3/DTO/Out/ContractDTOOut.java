package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractDTOOut {

    private Integer id;
    private Integer reservationId;
    private Integer apartmentId;
    private Integer userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double rentAmount;
    private String contractFilePath;
    private String status;            // ACTIVE EXPIRED TERMINATED
}
