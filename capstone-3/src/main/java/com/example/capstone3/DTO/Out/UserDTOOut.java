package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTOOut {

    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateOfBirth;
    private String maritalStatus;
    private Integer childrenCount;
    private LocalDateTime createdAt;
}
