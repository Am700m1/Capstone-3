package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ApartmentDTOIn;
import com.example.capstone3.DTO.Out.ApartmentDTOOut;
import com.example.capstone3.DTO.Out.ApartmentImageDTOOut;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.ApartmentImage;
import com.example.capstone3.Models.Building;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;

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
        Apartment apartment = new Apartment();
        apartment.setBuilding(building);
        apartment.setTitle(apartmentDTOIn.getTitle());
        apartment.setDescription(apartmentDTOIn.getDescription());
        apartment.setMonthlyRent(apartmentDTOIn.getMonthlyRent());
        apartment.setBedrooms(apartmentDTOIn.getBedrooms());
        apartment.setBathrooms(apartmentDTOIn.getBathrooms());
        apartment.setArea(apartmentDTOIn.getArea());
        apartment.setStatus("AVAILABLE");
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
        apartment.setBuilding(building);
        apartment.setTitle(apartmentDTOIn.getTitle());
        apartment.setDescription(apartmentDTOIn.getDescription());
        apartment.setMonthlyRent(apartmentDTOIn.getMonthlyRent());
        apartment.setBedrooms(apartmentDTOIn.getBedrooms());
        apartment.setBathrooms(apartmentDTOIn.getBathrooms());
        apartment.setArea(apartmentDTOIn.getArea());
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
        apartmentDTOOut.setDistrict(apartment.getBuilding().getDistrict());
        apartmentDTOOut.setTitle(apartment.getTitle());
        apartmentDTOOut.setDescription(apartment.getDescription());
        apartmentDTOOut.setMonthlyRent(apartment.getMonthlyRent());
        apartmentDTOOut.setBedrooms(apartment.getBedrooms());
        apartmentDTOOut.setBathrooms(apartment.getBathrooms());
        apartmentDTOOut.setArea(apartment.getArea());
        apartmentDTOOut.setStatus(apartment.getStatus());

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
}
