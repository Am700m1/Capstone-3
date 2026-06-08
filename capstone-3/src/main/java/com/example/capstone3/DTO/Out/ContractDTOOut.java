package com.example.capstone3.DTO.Out;

import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Enums.NegotiationStatus;
import com.example.capstone3.Enums.RenewalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractDTOOut {

    private Integer id;
    private Integer reservationId;
    private Integer apartmentId;
    private Integer userId;
    private String contractNumber;
    private ContractStatus contractStatus;
    private Double monthlyRent;
    private Double securityDeposit;
    private Boolean signed;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signedDate;
    private Boolean isJointContract;
    private String coTenantName;
    private Double rentPerPerson;
    private NegotiationStatus negotiationStatus;
    private Double requestedRent;
    private Double counterOfferRent;
    private String negotiationMessage;
    private LocalDateTime negotiationUpdatedAt;
    private String terminationReason;
    private Integer renewalRequestedMonths;
    private RenewalStatus renewalStatus;
}
