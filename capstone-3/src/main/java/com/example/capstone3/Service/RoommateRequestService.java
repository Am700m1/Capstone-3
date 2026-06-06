package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Enums.RoommateStatus;
import com.example.capstone3.Models.RoommateRequest;
import com.example.capstone3.Models.User;
import com.example.capstone3.Models.UserPreference;
import com.example.capstone3.Repository.RoommateRequestRepository;
import com.example.capstone3.Repository.UserPreferenceRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoommateRequestService {

    private final RoommateRequestRepository roommateRequestRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public void sendRoommateRequest(Integer senderId, Integer receiverId) {
        if (senderId.equals(receiverId)) {
            throw new ApiException("You cannot send a roommate request to yourself!");
        }

        User sender = userRepository.findUserById(senderId);
        User receiver = userRepository.findUserById(receiverId);

        if (sender == null || receiver == null) {
            throw new ApiException("Sender or Receiver not found!");
        }

        // Prevent sending a request if either user already has a roommate
        if (sender.getCurrentRoommateId() != null || receiver.getCurrentRoommateId() != null) {
            throw new ApiException("One of the users is no longer available for roommating.");
        }

        RoommateRequest request = new RoommateRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RoommateStatus.PENDING);
        request.setCreatedAt(LocalDate.now());

        roommateRequestRepository.save(request);
    }

    @Transactional
    public void acceptRoommateRequest(Integer receiverId, Integer requestId) {
        RoommateRequest request = roommateRequestRepository.findRoommateRequestById(requestId);

        if (request == null) {
            throw new ApiException("Roommate request not found!");
        }

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new ApiException("You are not authorized to accept this request!");
        }

        if (!request.getStatus().equals(RoommateStatus.PENDING)) {
            throw new ApiException("Only PENDING requests can be accepted!");
        }

        User sender = request.getSender();
        User receiver = request.getReceiver();

        // 1. Change this specific request status to ACCEPTED
        request.setStatus(RoommateStatus.ACCEPTED);
        roommateRequestRepository.save(request);

        // 2. Link the users together
        sender.setCurrentRoommateId(receiver.getId());
        receiver.setCurrentRoommateId(sender.getId());
        userRepository.save(sender);
        userRepository.save(receiver);

        // 3. Take them both off the market
        UserPreference senderPref = userPreferenceRepository.findUserPreferenceById(sender.getId());
        UserPreference receiverPref = userPreferenceRepository.findUserPreferenceById(receiver.getId());

        if (senderPref != null) {
            senderPref.setLookingForRoommate(false);
            userPreferenceRepository.save(senderPref);
        }
        if (receiverPref != null) {
            receiverPref.setLookingForRoommate(false);
            userPreferenceRepository.save(receiverPref);
        }

        // 4. The Domino Effect (Cancel all other pending requests involving these two users)
        cancelOtherPendingRequests(sender);
        cancelOtherPendingRequests(receiver);
    }

    // Helper Method for the Domino Effect
    private void cancelOtherPendingRequests(User user) {
        List<RoommateRequest> pendingRequests = roommateRequestRepository.findBySenderAndStatusOrReceiverAndStatus(
                user, RoommateStatus.PENDING, user, RoommateStatus.PENDING);

        for (RoommateRequest req : pendingRequests) {
            req.setStatus(RoommateStatus.CANCELLED);
            roommateRequestRepository.save(req);
        }
    }

    public void rejectRoommateRequest(Integer receiverId, Integer requestId) {
        RoommateRequest request = roommateRequestRepository.findRoommateRequestById(requestId);

        if (request == null) {
            throw new ApiException("Roommate request not found!");
        }

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new ApiException("You are not authorized to reject this request!");
        }

        if (!request.getStatus().equals(RoommateStatus.PENDING)) {
            throw new ApiException("Only PENDING requests can be rejected!");
        }

        request.setStatus(RoommateStatus.REJECTED);
        roommateRequestRepository.save(request);
    }

    @Transactional
    public void dissolveRoommate(Integer userId) {
        User user1 = userRepository.findUserById(userId);

        if (user1 == null || user1.getCurrentRoommateId() == null) {
            throw new ApiException("You do not currently have a roommate to dissolve.");
        }

        User user2 = userRepository.findUserById(user1.getCurrentRoommateId());

        // Unlink user 1
        user1.setCurrentRoommateId(null);
        userRepository.save(user1);

        // Unlink user 2
        if (user2 != null) {
            user2.setCurrentRoommateId(null);
            userRepository.save(user2);
        }
    }
}