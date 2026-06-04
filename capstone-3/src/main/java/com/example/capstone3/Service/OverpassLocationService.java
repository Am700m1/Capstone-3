package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.Out.ApartmentServicesDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OverpassLocationService {

    private static final String OVERPASS_URL = "https://gall.openstreetmap.de/api/interpreter";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApartmentServicesDTO analyzeApartmentLocation(double latitude, double longitude, int radiusMetres) {

        String query = buildQuery(latitude, longitude, radiusMetres);
        JsonNode elements = fetchFromOverpass(query);

        int hospitals    = 0;
        int schools      = 0;
        int supermarkets = 0;
        int pharmacies   = 0;
        int gyms         = 0;
        int restaurants  = 0;

        for (JsonNode element : elements) {
            JsonNode tags   = element.path("tags");
            String amenity  = tags.path("amenity").asText("");
            String shop     = tags.path("shop").asText("");
            String leisure  = tags.path("leisure").asText("");

            if (amenity.equals("hospital"))    hospitals++;
            if (amenity.equals("school"))      schools++;
            if (amenity.equals("pharmacy"))    pharmacies++;
            if (amenity.equals("restaurant") || amenity.equals("fast_food") || amenity.equals("cafe")) restaurants++;
            if (shop.equals("supermarket") || shop.equals("convenience")) supermarkets++;
            if (leisure.equals("fitness_centre") || leisure.equals("sports_centre")) gyms++;
        }

        ApartmentServicesDTO dto = new ApartmentServicesDTO();
        dto.setHospitalCount(hospitals);
        dto.setSchoolCount(schools);
        dto.setSupermarketCount(supermarkets);
        dto.setPharmacyCount(pharmacies);
        dto.setGymCount(gyms);
        dto.setRestaurantCount(restaurants);

        return dto;
    }

    private String buildQuery(double lat, double lng, int radius) {
        String around = "around:" + radius + "," + lat + "," + lng;
        return "[out:json][timeout:25];"
             + "("
             + "node[\"amenity\"=\"hospital\"](" + around + ");"
             + "way[\"amenity\"=\"hospital\"](" + around + ");"
             + "node[\"amenity\"=\"school\"](" + around + ");"
             + "way[\"amenity\"=\"school\"](" + around + ");"
             + "node[\"shop\"=\"supermarket\"](" + around + ");"
             + "node[\"shop\"=\"convenience\"](" + around + ");"
             + "node[\"amenity\"=\"pharmacy\"](" + around + ");"
             + "node[\"leisure\"=\"fitness_centre\"](" + around + ");"
             + "node[\"leisure\"=\"sports_centre\"](" + around + ");"
             + "node[\"amenity\"=\"restaurant\"](" + around + ");"
             + "node[\"amenity\"=\"fast_food\"](" + around + ");"
             + "node[\"amenity\"=\"cafe\"](" + around + ");"
             + ");"
             + "out center;";
    }

    private JsonNode fetchFromOverpass(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OVERPASS_URL, HttpMethod.POST, request, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("elements");

        } catch (Exception e) {
            throw new ApiException("Failed to fetch data from Overpass API: " + e.getMessage());
        }
    }
}
