package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.ContractStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractDTOOut {

    private Integer id;
    private Integer reservationId;
    private Integer apartmentId;
    private Integer userId;
    private String contractNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyRent;
    private Double securityDeposit;
    private Boolean signed;
    private LocalDate signedDate;
    private String pdfPath;
    private ContractStatus contractStatus;            // ACTIVE EXPIRED TERMINATED

    private Boolean isJointContract;
    private String coTenantName;
    private Double rentPerPerson;
}
