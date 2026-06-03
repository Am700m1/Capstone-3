package com.example.capstone3.DTO.Out;

import lombok.Data;

@Data
public class ApartmentImageDTOOut {

    private Integer id;
    private Integer apartmentId;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}
