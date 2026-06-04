package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ReviewDTOIn;
import com.example.capstone3.DTO.Out.ReviewDTOOut;
import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Enums.ReservationStatus;
import com.example.capstone3.Models.*;
import com.example.capstone3.Repository.*;
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
    private final ContractRepository contractRepository;

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

    public void addReview(ReviewDTOIn reviewDTOIn, Integer user_id) {
        User user = userRepository.findUserById(user_id);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Apartment apartment = apartmentRepository.findApartmentById(reviewDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        Contract contract = contractRepository.findContractByReservation_User_IdAndReservation_Apartment_IdAndStatus(
                user_id, reviewDTOIn.getApartmentId(), ContractStatus.ENDED);
        if (contract == null) {
            throw new ApiException("You can only review an apartment after your contract has ended");
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
        User user = review.getUser();
        if (user == null) {
            throw new ApiException("User not found");
        }
        Apartment apartment = review.getApartment();
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


    //^^^^^^^CRUD^^^^^^^^


}
