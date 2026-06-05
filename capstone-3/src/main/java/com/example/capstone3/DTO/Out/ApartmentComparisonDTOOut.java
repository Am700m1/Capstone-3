package com.example.capstone3.DTO.Out;

import lombok.Data;

import java.util.List;

@Data
public class ApartmentComparisonDTOOut {

    private List<Integer> apartmentIds;
    private String comparison;
}
