package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ApartmentImageDTOIn;
import com.example.capstone3.DTO.Out.ApartmentImageDTOOut;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.ApartmentImage;
import com.example.capstone3.Repository.ApartmentImageRepository;
import com.example.capstone3.Repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentImageService {

    private final ApartmentImageRepository apartmentImageRepository;
    private final ApartmentRepository apartmentRepository;

    public List<ApartmentImageDTOOut> getAll() {
        List<ApartmentImageDTOOut> apartmentImageDTOOuts = new ArrayList<>();
        for (ApartmentImage image : apartmentImageRepository.findAll()) {
            apartmentImageDTOOuts.add(convertToDTO(image));
        }
        return apartmentImageDTOOuts;
    }

    public ApartmentImageDTOOut getApartmentImage(Integer id) {
        ApartmentImage image = apartmentImageRepository.findApartmentImageById(id);
        if (image == null) {
            throw new ApiException("Apartment image not found");
        }
        return convertToDTO(image);
    }

    public void addApartmentImage(ApartmentImageDTOIn apartmentImageDTOIn) {
        Apartment apartment = apartmentRepository.findApartmentById(apartmentImageDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        ApartmentImage image = new ApartmentImage();
        image.setApartment(apartment);
        image.setImageUrl(apartmentImageDTOIn.getImageUrl());
        image.setIsPrimary(apartmentImageDTOIn.getIsPrimary());
        apartmentImageRepository.save(image);
    }

    public void updateApartmentImage(Integer id, ApartmentImageDTOIn apartmentImageDTOIn) {
        ApartmentImage image = apartmentImageRepository.findApartmentImageById(id);
        if (image == null) {
            throw new ApiException("Apartment image not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(apartmentImageDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        image.setApartment(apartment);
        image.setImageUrl(apartmentImageDTOIn.getImageUrl());
        image.setIsPrimary(apartmentImageDTOIn.getIsPrimary());
        apartmentImageRepository.save(image);
    }

    public void deleteApartmentImage(Integer id) {
        ApartmentImage image = apartmentImageRepository.findApartmentImageById(id);
        if (image == null) {
            throw new ApiException("Apartment image not found");
        }
        apartmentImageRepository.deleteById(id);
    }

    public ApartmentImageDTOOut convertToDTO(ApartmentImage image) {
        ApartmentImageDTOOut apartmentImageDTOOut = new ApartmentImageDTOOut();
        apartmentImageDTOOut.setId(image.getId());
        apartmentImageDTOOut.setApartmentId(image.getApartment().getId());
        apartmentImageDTOOut.setImageUrl(image.getImageUrl());
        apartmentImageDTOOut.setIsPrimary(image.getIsPrimary());
        apartmentImageDTOOut.setDisplayOrder(image.getDisplayOrder());
        return apartmentImageDTOOut;
    }
}
