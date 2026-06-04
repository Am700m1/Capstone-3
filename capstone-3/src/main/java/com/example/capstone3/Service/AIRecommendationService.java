package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.Out.ApartmentServicesDTO;
import com.example.capstone3.DTO.Out.CommuteAnalysisDTOOut;
import com.example.capstone3.DTO.Out.RecommendationResponseDTOOut;
import com.example.capstone3.Enums.PreferenceLevel;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.Review;
import com.example.capstone3.Models.User;
import com.example.capstone3.Models.UserPreference;
import com.example.capstone3.Repository.ReviewRepository;
import com.example.capstone3.Repository.UserPreferenceRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIRecommendationService {

    private final ApartmentFilteringService filteringService;
    private final OverpassLocationService overpassLocationService;
    private final OsrmCommuteService osrmCommuteService;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AiService aiService;

    public RecommendationResponseDTOOut recommend(Integer userId, int radiusMetres) {

        UserPreference preferences = userPreferenceRepository.findUserPreferenceByUserId(userId);
        if (preferences == null) {
            throw new ApiException("User preferences not found. Please add your preferences first.");
        }

        User user = userRepository.findUserById(userId);

        List<Apartment> apartments = filteringService.filterByPreferences(preferences);
        if (apartments.isEmpty()) {
            throw new ApiException("No available apartments match your preferences.");
        }

        String prompt = buildPrompt(apartments, preferences, user, radiusMetres);
        String aiResponse = aiService.generateText(prompt);

        RecommendationResponseDTOOut response = new RecommendationResponseDTOOut();
        response.setUserId(userId);
        response.setRecommendation(aiResponse);

        return response;
    }

    private String buildPrompt(List<Apartment> apartments, UserPreference preferences, User user, int radiusMetres) {

        apartments = apartments.stream()
                .limit(2)
                .toList();

        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a smart rental apartment recommendation assistant in Saudi Arabia.\n");
        prompt.append("Analyze the apartments below and recommend the best options for this user.\n\n");

        // User profile
        if (user != null) {
            prompt.append("=== USER PROFILE ===\n");
            prompt.append("Marital Status: ").append(user.getMaritalStatus() != null ? user.getMaritalStatus() : "Not specified").append("\n");
            prompt.append("Children: ").append(user.getChildrenCount() != null ? user.getChildrenCount() : 0).append("\n\n");
        }

        // User preferences
        prompt.append("=== USER PREFERENCES ===\n");
        prompt.append("Max Budget: SAR ").append(preferences.getMaxBudget()).append(" per month\n");
        prompt.append("Preferred Bedrooms: ").append(preferences.getPreferredBedrooms() != null ? preferences.getPreferredBedrooms() : "Any").append("\n");
        prompt.append("Preferred Bathrooms: ").append(preferences.getPreferredBathrooms() != null ? preferences.getPreferredBathrooms() : "Any").append("\n");
        prompt.append("Preferred District: ").append(preferences.getPreferredDistrict() != null ? preferences.getPreferredDistrict() : "No preference").append("\n");
        prompt.append("Requires Parking: ").append(Boolean.TRUE.equals(preferences.getRequiresParking()) ? "Yes" : "No").append("\n");
        prompt.append("Requires Elevator: ").append(Boolean.TRUE.equals(preferences.getRequiresElevator()) ? "Yes" : "No").append("\n");
        prompt.append("Requires Furnished: ").append(Boolean.TRUE.equals(preferences.getRequiresFurnished()) ? "Yes" : "No").append("\n");
        prompt.append("Max Commute: ").append(preferences.getMaxCommuteMinutes() != null ? preferences.getMaxCommuteMinutes() + " minutes" : "Not specified").append("\n");

        // Amenity importance
        prompt.append("\n=== AMENITY IMPORTANCE ===\n");
        prompt.append("Hospital Importance: ").append(formatPreference(preferences.getHospitalPreference())).append("\n");
        prompt.append("School Importance: ").append(formatPreference(preferences.getSchoolPreference())).append("\n");
        prompt.append("Gym Importance: ").append(formatPreference(preferences.getGymPreference())).append("\n");
        prompt.append("Cafes Importance: ").append(formatPreference(preferences.getCafesPreference())).append("\n");
        prompt.append("Public Transport Importance: ").append(formatPreference(preferences.getPublicTransportPreference())).append("\n\n");

        // Apartments
        prompt.append("=== AVAILABLE APARTMENTS ===\n");

        for (int i = 0; i < apartments.size(); i++) {
            Apartment apt = apartments.get(i);
            double aptLat = apt.getBuilding().getLatitude();
            double aptLng = apt.getBuilding().getLongitude();

            prompt.append("\n--- Apartment ").append(i + 1).append(" ---\n");
            prompt.append("Title: ").append(apt.getTitle()).append("\n");
            prompt.append("District: ").append(apt.getBuilding().getDistrict()).append("\n");
            prompt.append("Monthly Rent: SAR ").append(apt.getMonthlyRent()).append("\n");
            prompt.append("Bedrooms: ").append(apt.getBedrooms()).append("\n");
            prompt.append("Bathrooms: ").append(apt.getBathrooms()).append("\n");
            prompt.append("Area: ").append(apt.getArea()).append(" sqm\n");
            prompt.append("Furnished: ").append(Boolean.TRUE.equals(apt.getFurnished()) ? "Yes" : "No").append("\n");
            prompt.append("Parking: ").append(Boolean.TRUE.equals(apt.getBuilding().getHasParking()) ? "Yes" : "No").append("\n");
            prompt.append("Elevator: ").append(Boolean.TRUE.equals(apt.getBuilding().getHasElevator()) ? "Yes" : "No").append("\n");
            prompt.append("Water Included: ").append(Boolean.TRUE.equals(apt.getWaterIncluded()) ? "Yes" : "No").append("\n");
            prompt.append("Internet Included: ").append(Boolean.TRUE.equals(apt.getInternetIncluded()) ? "Yes" : "No").append("\n");
            prompt.append("Electricity Included: ").append(Boolean.TRUE.equals(apt.getElectricityIncluded()) ? "Yes" : "No").append("\n");

            if (apt.getDescription() != null && !apt.getDescription().isBlank()) {
                prompt.append("Description: ").append(apt.getDescription()).append("\n");
            }

            // Reviews
            List<Review> reviews = reviewRepository.findReviewByApartmentId(apt.getId());
            if (!reviews.isEmpty()) {
                double avg = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0);
                prompt.append("Average Rating: ").append(String.format("%.1f", avg)).append("/5 (")
                        .append(reviews.size()).append(" reviews)\n");
            } else {
                prompt.append("Reviews: No reviews yet\n");
            }

            // Nearby amenities from Overpass
            try {
                ApartmentServicesDTO amenities = overpassLocationService.analyzeApartmentLocation(aptLat, aptLng, radiusMetres);
                prompt.append("Nearby Amenities (within ").append(radiusMetres).append("m): ");
                prompt.append("Hospitals: ").append(amenities.getHospitalCount()).append(", ");
                prompt.append("Schools: ").append(amenities.getSchoolCount()).append(", ");
                prompt.append("Supermarkets: ").append(amenities.getSupermarketCount()).append(", ");
                prompt.append("Pharmacies: ").append(amenities.getPharmacyCount()).append(", ");
                prompt.append("Gyms: ").append(amenities.getGymCount()).append(", ");
                prompt.append("Restaurants: ").append(amenities.getRestaurantCount()).append("\n");
            } catch (Exception e) {
                prompt.append("Nearby Amenities: Data unavailable\n");
            }

            // Commute from OSRM
            try {
                CommuteAnalysisDTOOut commute = osrmCommuteService.analyzeCommute(
                        aptLat, aptLng,
                        preferences.getWorkLatitude(), preferences.getWorkLongitude()
                );
                prompt.append("Commute to Work: ").append(commute.getDurationMinutes()).append(" minutes (")
                        .append(commute.getDistanceKm()).append(" km)\n");
            } catch (Exception e) {
                prompt.append("Commute to Work: Data unavailable\n");
            }
        }

        // Task
        prompt.append("\n=== YOUR TASK ===\n");
        prompt.append("Based on everything above, provide your recommendation:\n\n");
        prompt.append("1. TOP 3 RANKING\n");
        prompt.append("   Rank the top 3 apartments from best to worst.\n");
        prompt.append("   For each: state the title, district, and 2-3 sentences explaining why it suits this user.\n\n");
        prompt.append("2. FINAL RECOMMENDATION\n");
        prompt.append("   Recommend the single best apartment in 3-4 sentences.\n");
        prompt.append("   When evaluating, consider:\n");
        prompt.append("   - Budget fit and included utilities (water, internet, electricity)\n");
        prompt.append("   - Family situation and children count\n");
        prompt.append("   - Amenity importance (prioritize what the user marked as HIGH)\n");
        prompt.append("   - Commute convenience\n");
        prompt.append("   - District preference (treat as a positive factor, not a requirement)\n");
        prompt.append("   - Apartment description and overall quality\n");
        prompt.append("   - Review rating if available\n\n");
        prompt.append("Write in a clear, friendly, and helpful tone.");

        return prompt.toString();
    }

    private String formatPreference(PreferenceLevel level) {
        if (level == null) return "Not specified";
        return switch (level) {
            case VERY_IMPORTANT -> "HIGH";
            case PREFERRED -> "MEDIUM";
            case NOT_IMPORTANT -> "LOW";
        };
    }

}
