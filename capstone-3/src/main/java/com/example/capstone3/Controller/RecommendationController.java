package com.example.capstone3.Controller;

import com.example.capstone3.DTO.In.RecommendationRequestDTOIn;
import com.example.capstone3.DTO.Out.RecommendationResponseDTOOut;
import com.example.capstone3.Service.AIRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final AIRecommendationService aiRecommendationService;

    @PostMapping("/recommend")
    public RecommendationResponseDTOOut recommend(@RequestBody @Valid RecommendationRequestDTOIn request) {
        return aiRecommendationService.recommend(request.getUserId(), request.getRadiusMetres());
    }
}
