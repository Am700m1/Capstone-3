package com.example.capstone3.DTO.In;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTOIn {

    @NotEmpty(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotEmpty(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Gender must be exactly MALE or FEMALE")
    private String gender;

    private LocalDateTime dateOfBirth;

    @NotEmpty(message = "Marital status is required")
    @Size(max = 10, message = "Marital status must not exceed 10 characters")
    private String maritalStatus;

    @NotNull(message = "Children count is required")
    @Min(value = 0, message = "Children count cannot be negative")
    private Integer childrenCount;
}
