package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.Out.RoommateMatchDTOOut;
import com.example.capstone3.Models.User;
import com.example.capstone3.Models.UserPreference;
import com.example.capstone3.Repository.UserPreferenceRepository;
import com.example.capstone3.Repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoommateService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    public List<RoommateMatchDTOOut> getAiRoommateMatches(Integer userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        UserPreference requesterPref = userPreferenceRepository.findUserPreferenceById(userId); // Assuming preference ID matches user ID or use a custom query

        if (requesterPref == null || !requesterPref.getLookingForRoommate()) {
            throw new ApiException("You must create a user preference and set lookingForRoommate to true to use this feature.");
        }

        // 1. Database Pre-Filtering (+/- 1500 SAR budget variance)
        Double minBudget = requesterPref.getRoommateBudget() - 1500.0;
        Double maxBudget = requesterPref.getRoommateBudget() + 1500.0;

        List<UserPreference> candidates = userPreferenceRepository.findPotentialRoommates(
                user.getGender(), user.getId(), minBudget, maxBudget);

        if (candidates.isEmpty()) {
            throw new ApiException("No potential roommates found in your budget and gender criteria right now.");
        }

        // Limit to top 10 candidates to save AI tokens
        if (candidates.size() > 10) {
            candidates = candidates.subList(0, 10);
        }

        // 2. AI Behavioral Matchmaking
        try {
            String aiJsonString = aiService.getRoommateMatches(requesterPref, candidates);

            // Parse the JSON array into our DTO list
            List<RoommateMatchDTOOut> matches = objectMapper.readValue(aiJsonString, new TypeReference<List<RoommateMatchDTOOut>>() {});

            // 3. Map the candidate's actual name to the final response
            for (RoommateMatchDTOOut match : matches) {
                User candidate = userRepository.findUserById(match.getCandidateId());
                if (candidate != null) {
                    match.setCandidateName(candidate.getFullName());
                }
            }
            return matches;

        } catch (Exception e) {
            throw new ApiException("Failed to parse AI matches: " + e.getMessage());
        }
    }
}