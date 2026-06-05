package com.example.capstone3.Controller;

import com.example.capstone3.Service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final AIRecommendationService aiRecommendationService;

    @GetMapping("/recommend/{userId}")
    public ResponseEntity<?> recommend(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(aiRecommendationService.recommend(userId, 3000));
    }
}
