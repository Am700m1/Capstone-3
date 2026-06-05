package com.example.capstone3.Controller;

import com.example.capstone3.Service.AiApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AiApartmentController {

    private final AiApartmentService aiApartmentService;

    @GetMapping("/api/v1/ai/apartments/review-summary/{apartmentId}")
    public ResponseEntity<?> getReviewSummary(@PathVariable Integer apartmentId) {
        return ResponseEntity.status(200).body(aiApartmentService.getReviewSummary(apartmentId));
    }

    @GetMapping("/api/v1/ai/apartments/neighborhood-summary/{apartmentId}")
    public ResponseEntity<?> getNeighborhoodSummary(@PathVariable Integer apartmentId) {
        return ResponseEntity.status(200).body(aiApartmentService.getNeighborhoodSummary(apartmentId));
    }

    @GetMapping("/api/v1/ai/apartments/price-suggestion/{apartmentId}")
    public ResponseEntity<?> getPriceSuggestion(@PathVariable Integer apartmentId) {
        return ResponseEntity.status(200).body(aiApartmentService.getPriceSuggestion(apartmentId));
    }

    @GetMapping("/api/v1/ai/owners/reputation-summary/{ownerId}")
    public ResponseEntity<?> getOwnerReputationSummary(@PathVariable Integer ownerId) {
        return ResponseEntity.status(200).body(aiApartmentService.getOwnerReputationSummary(ownerId));
    }

    @GetMapping("/api/v1/ai/apartments/compare/{id1}/{id2}")
    public ResponseEntity<?> compareTwoApartments(@PathVariable Integer id1, @PathVariable Integer id2) {
        return ResponseEntity.status(200).body(aiApartmentService.compareApartments(List.of(id1, id2)));
    }

}
