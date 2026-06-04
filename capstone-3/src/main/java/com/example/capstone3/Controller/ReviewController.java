package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ReviewDTOIn;
import com.example.capstone3.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/get")
    public ResponseEntity<?> getReviews() {
        return ResponseEntity.status(200).body(reviewService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReview(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reviewService.getReview(id));
    }

    @PostMapping("/add/{user_id}")
    public ResponseEntity<?> addReview(@RequestBody @Valid ReviewDTOIn reviewDTOIn, @PathVariable Integer user_id) {
        reviewService.addReview(reviewDTOIn, user_id);
        return ResponseEntity.status(200).body(new ApiResponse("Review added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Integer id, @RequestBody @Valid ReviewDTOIn reviewDTOIn) {
        reviewService.updateReview(id, reviewDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Review updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(200).body(new ApiResponse("Review deleted successfully"));
    }
}
