package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ReviewDTOIn;
import com.example.capstone3.DTO.Out.ReviewDTOOut;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.Review;
import com.example.capstone3.Models.User;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.ReviewRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;

    public List<ReviewDTOOut> getAll() {
        List<ReviewDTOOut> reviewDTOOuts = new ArrayList<>();
        for (Review review : reviewRepository.findAll()) {
            reviewDTOOuts.add(convertToDTO(review));
        }
        return reviewDTOOuts;
    }

    public ReviewDTOOut getReview(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        return convertToDTO(review);
    }

    public void addReview(ReviewDTOIn reviewDTOIn) {
        User user = userRepository.findUserById(reviewDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(reviewDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        Review review = new Review();
        review.setUser(user);
        review.setApartment(apartment);
        review.setRating(reviewDTOIn.getRating());
        review.setComment(reviewDTOIn.getComment());
        reviewRepository.save(review);
    }

    public void updateReview(Integer id, ReviewDTOIn reviewDTOIn) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        User user = userRepository.findUserById(reviewDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(reviewDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        review.setUser(user);
        review.setApartment(apartment);
        review.setRating(reviewDTOIn.getRating());
        review.setComment(reviewDTOIn.getComment());
        reviewRepository.save(review);
    }

    public void deleteReview(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    public ReviewDTOOut convertToDTO(Review review) {
        ReviewDTOOut reviewDTOOut = new ReviewDTOOut();
        reviewDTOOut.setId(review.getId());
        reviewDTOOut.setUserId(review.getUser().getId());
        reviewDTOOut.setApartmentId(review.getApartment().getId());
        reviewDTOOut.setRating(review.getRating());
        reviewDTOOut.setComment(review.getComment());
        reviewDTOOut.setCreatedAt(review.getCreatedAt());
        return reviewDTOOut;
    }
}
