package com.example.capstone3.Controller;

import com.example.capstone3.DTO.In.ApartmentServicesRequestDTO;
import com.example.capstone3.DTO.Out.ApartmentServicesDTO;
import com.example.capstone3.Service.OverpassLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationAnalysisController {

    private final OverpassLocationService overpassLocationService;

    @PostMapping("/analyze")
    public ApartmentServicesDTO analyzeArea(@RequestBody @Valid ApartmentServicesRequestDTO request) {
        return overpassLocationService.analyzeApartmentLocation(request.getLatitude(), request.getLongitude(), request.getRadiusMetres());
    }
}
