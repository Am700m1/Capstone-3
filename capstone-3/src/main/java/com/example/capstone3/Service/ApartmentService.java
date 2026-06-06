package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ApartmentDTOIn;
import com.example.capstone3.DTO.Out.ApartmentDTOOut;
import com.example.capstone3.DTO.Out.ApartmentImageDTOOut;
import com.example.capstone3.DTO.Out.LowRatedApartmentDTOOut;
import com.example.capstone3.DTO.Out.UnderpricedApartmentDTOOut;
import com.example.capstone3.Enums.ApartmentStatus;
import com.example.capstone3.Models.*;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.BuildingRepository;
import com.example.capstone3.Repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;
    private final OwnerRepository ownerRepository;
    private final AiService aiService;

    public List<ApartmentDTOOut> getAll() {
        List<ApartmentDTOOut> apartmentDTOOuts = new ArrayList<>();
        for (Apartment apartment : apartmentRepository.findAll()) {
            apartmentDTOOuts.add(convertToDTO(apartment));
        }
        return apartmentDTOOuts;
    }

    public ApartmentDTOOut getApartment(Integer id) {
        Apartment apartment = apartmentRepository.findApartmentById(id);
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        return convertToDTO(apartment);
    }

    public void addApartment(ApartmentDTOIn apartmentDTOIn) {
        Building building = buildingRepository.findBuildingById(apartmentDTOIn.getBuildingId());
        if (building == null) {
            throw new ApiException("Building not found");
        }
        Owner owner = ownerRepository.findOwnerById(apartmentDTOIn.getOwnerId());
        if (owner == null) {
            throw new ApiException("Owner not found");
        }
        if (!building.getOwner().getId().equals(owner.getId())) {
            throw new ApiException("Owner does not own this building");
        }
        Apartment apartment = new Apartment();
        apartment.setBuilding(building);
        apartment.setOwner(owner);
        apartment.setTitle(apartmentDTOIn.getTitle());
        apartment.setDescription(apartmentDTOIn.getDescription());
        apartment.setMonthlyRent(apartmentDTOIn.getMonthlyRent());
        apartment.setBedrooms(apartmentDTOIn.getBedrooms());
        apartment.setBathrooms(apartmentDTOIn.getBathrooms());
        apartment.setArea(apartmentDTOIn.getArea());
        apartment.setFloorNumber(apartmentDTOIn.getFloorNumber());
        apartment.setFurnished(apartmentDTOIn.getFurnished());
        apartment.setAvailable(apartmentDTOIn.getAvailable() == null ? true : apartmentDTOIn.getAvailable());
        apartment.setAvailableFrom(apartmentDTOIn.getAvailableFrom());
        apartment.setAllowedTenantType(apartmentDTOIn.getAllowedTenantType());
        apartment.setWaterIncluded(apartmentDTOIn.getWaterIncluded());
        apartment.setInternetIncluded(apartmentDTOIn.getInternetIncluded());
        apartment.setElectricityIncluded(apartmentDTOIn.getElectricityIncluded());
        apartment.setStatus(ApartmentStatus.AVAILABLE);
        apartmentRepository.save(apartment);
    }

    public void updateApartment(Integer id, ApartmentDTOIn apartmentDTOIn) {
        Apartment apartment = apartmentRepository.findApartmentById(id);
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        Building building = buildingRepository.findBuildingById(apartmentDTOIn.getBuildingId());
        if (building == null) {
            throw new ApiException("Building not found");
        }
        Owner owner = ownerRepository.findOwnerById(apartmentDTOIn.getOwnerId());
        if (owner == null) {
            throw new ApiException("Owner not found");
        }
        if (!building.getOwner().getId().equals(owner.getId())) {
            throw new ApiException("Owner does not own this building");
        }
        apartment.setBuilding(building);
        apartment.setOwner(owner);
        apartment.setTitle(apartmentDTOIn.getTitle());
        apartment.setDescription(apartmentDTOIn.getDescription());
        apartment.setMonthlyRent(apartmentDTOIn.getMonthlyRent());
        apartment.setBedrooms(apartmentDTOIn.getBedrooms());
        apartment.setBathrooms(apartmentDTOIn.getBathrooms());
        apartment.setArea(apartmentDTOIn.getArea());
        apartment.setFloorNumber(apartmentDTOIn.getFloorNumber());
        apartment.setFurnished(apartmentDTOIn.getFurnished());
        apartment.setAvailable(apartmentDTOIn.getAvailable());
        apartment.setAvailableFrom(apartmentDTOIn.getAvailableFrom());
        apartment.setAllowedTenantType(apartmentDTOIn.getAllowedTenantType());
        apartment.setWaterIncluded(apartmentDTOIn.getWaterIncluded());
        apartment.setInternetIncluded(apartmentDTOIn.getInternetIncluded());
        apartment.setElectricityIncluded(apartmentDTOIn.getElectricityIncluded());
        apartmentRepository.save(apartment);
    }

    public void deleteApartment(Integer id) {
        Apartment apartment = apartmentRepository.findApartmentById(id);
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        apartmentRepository.deleteById(id);
    }

    public ApartmentDTOOut convertToDTO(Apartment apartment) {
        ApartmentDTOOut apartmentDTOOut = new ApartmentDTOOut();
        apartmentDTOOut.setId(apartment.getId());
        apartmentDTOOut.setBuildingId(apartment.getBuilding().getId());
        apartmentDTOOut.setOwnerId(apartment.getOwner().getId());
        apartmentDTOOut.setDistrict(apartment.getBuilding().getDistrict());
        apartmentDTOOut.setTitle(apartment.getTitle());
        apartmentDTOOut.setDescription(apartment.getDescription());
        apartmentDTOOut.setMonthlyRent(apartment.getMonthlyRent());
        apartmentDTOOut.setBedrooms(apartment.getBedrooms());
        apartmentDTOOut.setBathrooms(apartment.getBathrooms());
        apartmentDTOOut.setArea(apartment.getArea());
        apartmentDTOOut.setFloorNumber(apartment.getFloorNumber());
        apartmentDTOOut.setStatus(apartment.getStatus() == null ? null : apartment.getStatus().name());
        apartmentDTOOut.setFurnished(apartment.getFurnished());
        apartmentDTOOut.setAvailable(apartment.getAvailable());
        apartmentDTOOut.setAvailableFrom(apartment.getAvailableFrom());
        apartmentDTOOut.setAllowedTenantType(apartment.getAllowedTenantType());
        apartmentDTOOut.setWaterIncluded(apartment.getWaterIncluded());
        apartmentDTOOut.setInternetIncluded(apartment.getInternetIncluded());
        apartmentDTOOut.setElectricityIncluded(apartment.getElectricityIncluded());

        List<ApartmentImageDTOOut> imageDTOOuts = new ArrayList<>();
        for (ApartmentImage image : apartment.getImages()) {
            ApartmentImageDTOOut imageDTOOut = new ApartmentImageDTOOut();
            imageDTOOut.setId(image.getId());
            imageDTOOut.setApartmentId(apartment.getId());
            imageDTOOut.setImageUrl(image.getImageUrl());
            imageDTOOut.setIsPrimary(image.getIsPrimary());
            imageDTOOut.setDisplayOrder(image.getDisplayOrder());
            imageDTOOuts.add(imageDTOOut);
        }
        apartmentDTOOut.setImages(imageDTOOuts);

        return apartmentDTOOut;
    }


    //^^^^^^^CRUD^^^^^^^^


    public List<UnderpricedApartmentDTOOut> getUnderpricedApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        List<UnderpricedApartmentDTOOut> result = new ArrayList<>();

        for (Apartment apartment : apartments) {

            // --- Average days to get reserved (Speed) ---
            List<Reservation> reservations = apartment.getReservations();
            double avgDaysToReserve = 0;
            if (!reservations.isEmpty()) {
                long totalDays = 0;
                for (Reservation reservation : reservations) {
                    totalDays += ChronoUnit.DAYS.between(reservation.getCreatedAt().toLocalDate(), reservation.getReservationDate());
                }
                avgDaysToReserve = (double) totalDays / reservations.size();
            }

            // --- Average rating (Reviews) ---
            List<Review> reviews = apartment.getReviews();
            double avgRating = 0;
            if (!reviews.isEmpty()) {
                double totalRating = 0;
                for (Review review : reviews) {
                    totalRating += review.getRating();
                }
                avgRating = totalRating / reviews.size();
            }

            // --- Filter: must have reservations and good reviews ---
            if (reservations.isEmpty() || avgRating < 4.0) continue;

            // --- Weighted score: Speed(20%) + Rating(50%) + Demand(30%) ---
            double speedScore   = 1.0 / (1.0 + avgDaysToReserve);   // fewer days = higher score
            double ratingScore  = avgRating / 5.0;                   // normalize to 0-1
            double demandScore  = Math.min(reservations.size() / 10.0, 1.0); // cap at 10 reservations

            double finalScore = (speedScore * 0.20) + (ratingScore * 0.50) + (demandScore * 0.30);

            UnderpricedApartmentDTOOut dto = new UnderpricedApartmentDTOOut();
            dto.setId(apartment.getId());
            dto.setTitle(apartment.getTitle());
            dto.setDistrict(apartment.getBuilding().getDistrict());
            dto.setMonthlyRent(apartment.getMonthlyRent());
            dto.setBedrooms(apartment.getBedrooms());
            dto.setBathrooms(apartment.getBathrooms());
            dto.setArea(apartment.getArea());
            dto.setTotalReservations(reservations.size());
            dto.setAvgDaysToReserve(Math.round(avgDaysToReserve * 100.0) / 100.0);
            dto.setAverageRating(Math.round(avgRating * 100.0) / 100.0);
            dto.setScore(Math.round(finalScore * 100.0) / 100.0);
            result.add(dto);
        }

        if (result.isEmpty()) {
            throw new ApiException("No underpriced apartments found");
        }

        result.sort(Comparator.comparingDouble(UnderpricedApartmentDTOOut::getScore).reversed());
        return result;
    }


    public List<LowRatedApartmentDTOOut> getLowRatedApartmentsByBuilding(Integer ownerId, Integer buildingId) {
        Owner owner = ownerRepository.findOwnerById(ownerId);
        if (owner == null) {
            throw new ApiException("Owner not found");
        }

        Building building = buildingRepository.findBuildingById(buildingId);
        if (building == null) {
            throw new ApiException("Building not found");
        }

        // Security check: owner must own this building
        if (!building.getOwner().getId().equals(ownerId)) {
            throw new ApiException("You do not have permission to view this building");
        }

        List<LowRatedApartmentDTOOut> result = new ArrayList<>();

        for (Apartment apartment : building.getApartments()) {

            List<Review> reviews = apartment.getReviews();
            if (reviews.isEmpty()) continue;

            // Calculate average rating
            double totalRating = 0;
            for (Review review : reviews) {
                totalRating += review.getRating();
            }
            double avgRating = totalRating / reviews.size();

            // Only include apartments with average rating below 2.0
            if (avgRating >= 2.0) continue;

            // Build comments string to send to AI
            StringBuilder comments = new StringBuilder();
            for (Review review : reviews) {
                comments.append("- ").append(review.getComment()).append("\n");
            }

            // Call AI to summarize the issues
            String aiSummary = getAiSummary(apartment.getTitle(), comments.toString());

            LowRatedApartmentDTOOut dto = new LowRatedApartmentDTOOut();
            dto.setId(apartment.getId());
            dto.setTitle(apartment.getTitle());
            dto.setDistrict(building.getDistrict());
            dto.setMonthlyRent(apartment.getMonthlyRent());
            dto.setBedrooms(apartment.getBedrooms());
            dto.setBathrooms(apartment.getBathrooms());
            dto.setAverageRating(Math.round(avgRating * 100.0) / 100.0);
            dto.setAiSummary(aiSummary);
            result.add(dto);
        }

        if (result.isEmpty()) {
            throw new ApiException("No low rated apartments found in this building");
        }

        result.sort(Comparator.comparingDouble(LowRatedApartmentDTOOut::getAverageRating));
        return result;
    }

    private String getAiSummary(String apartmentTitle, String comments) {
        String prompt = "You are analyzing tenant reviews for an apartment called \"" + apartmentTitle + "\".\n" +
                "Here are the review comments:\n" + comments + "\n" +
                "Summarize the main issues tenants are complaining about in one sentence starting with \"Main issues: \"";
        return aiService.generateText(prompt);
    }


    public void toggleMaintenanceMode(Integer ownerId, Integer apartmentId){
        Owner owner = ownerRepository.findOwnerById(ownerId);
        Apartment apartment = apartmentRepository.findApartmentById(apartmentId);

        if(owner == null){
            throw new ApiException("Owner not found");
        }

        if (apartment == null) {
            throw new ApiException("Apartment not found!");
        }

        if(!apartment.getOwner().getId().equals(ownerId)){
            throw new ApiException("You are not authorized to this action!");
        }

        apartment.setAvailable(false);
        apartment.setStatus(ApartmentStatus.UNDER_MAINTENANCE);
        apartmentRepository.save(apartment);
    }


    public void toggleAvailableMode(Integer ownerId, Integer apartmentId){
        Owner owner = ownerRepository.findOwnerById(ownerId);
        Apartment apartment = apartmentRepository.findApartmentById(apartmentId);

        if(owner == null){
            throw new ApiException("Owner not found");
        }

        if (apartment == null) {
            throw new ApiException("Apartment not found!");
        }

        if(!apartment.getOwner().getId().equals(ownerId)){
            throw new ApiException("You are not authorized to this action!");
        }

        apartment.setAvailable(true);
        apartment.setStatus(ApartmentStatus.AVAILABLE);
        apartmentRepository.save(apartment);
    }

    // this method show the owner apartments and their status. The apartments will be grouped by status.
    public Map<ApartmentStatus, List<ApartmentDTOOut>> getOwnerDashboard(Integer ownerId) {
        Owner owner = ownerRepository.findOwnerById(ownerId);

        if (owner == null) {
            throw new ApiException("Owner not found!");
        }

        List<Apartment> apartments = apartmentRepository.findApartmentsByOwnerId(ownerId);

        Map<ApartmentStatus, List<ApartmentDTOOut>> groupedApartments = new HashMap<>();

        for (ApartmentStatus status : ApartmentStatus.values()) {
            groupedApartments.put(status, new ArrayList<>());
        }

        for (Apartment apartment : apartments) {
            ApartmentDTOOut dto = convertToDTO(apartment); // Assuming you have this method
            groupedApartments.get(apartment.getStatus()).add(dto);
        }

        return groupedApartments;
    }

    public List<ApartmentDTOOut> searchApartments(Double minRent,Double maxRent, Integer bedrooms, String district, Boolean isFurnished){
        List<Apartment> apartments = apartmentRepository.searchAvailableApartments(minRent, maxRent, bedrooms, district, isFurnished);

        if(apartments.isEmpty()){
            throw new ApiException("No apartments were found matching your search criteria!");
        }

        List<ApartmentDTOOut> apartmentDTOOuts = new ArrayList<>();
        for(Apartment apartment: apartments){
            apartmentDTOOuts.add(convertToDTO(apartment));
        }

        return apartmentDTOOuts;

    }
}
