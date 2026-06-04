package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.Out.CommuteAnalysisDTOOut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OsrmCommuteService {

    // Public OSRM demo server — no API key required
    private static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CommuteAnalysisDTOOut analyzeCommute(double apartmentLatitude, double apartmentLongitude,
                                                double workLatitude, double workLongitude) {
        try {
            // OSRM expects: longitude,latitude (not lat,lng)
            String url = OSRM_URL
                    + apartmentLongitude + "," + apartmentLatitude
                    + ";"
                    + workLongitude + "," + workLatitude
                    + "?overview=false";

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode route = root.path("routes").path(0);

            double distanceMetres = route.path("distance").asDouble();
            double durationSeconds = route.path("duration").asDouble();

            CommuteAnalysisDTOOut dto = new CommuteAnalysisDTOOut();
            dto.setDistanceKm(Math.round((distanceMetres / 1000.0) * 10.0) / 10.0);
            dto.setDurationMinutes((int) Math.ceil(durationSeconds / 60.0));

            return dto;

        } catch (Exception e) {
            throw new ApiException("Failed to calculate commute from OSRM: " + e.getMessage());
        }
    }
}
