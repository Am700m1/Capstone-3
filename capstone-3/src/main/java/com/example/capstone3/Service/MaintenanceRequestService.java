package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.MaintenanceRequestDTOIn;
import com.example.capstone3.DTO.Out.MaintenanceRequestDTOOut;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.MaintenanceRequest;
import com.example.capstone3.Models.User;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.MaintenanceRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;

    public List<MaintenanceRequestDTOOut> getAll() {
        List<MaintenanceRequestDTOOut> maintenanceRequestDTOOuts = new ArrayList<>();
        for (MaintenanceRequest maintenanceRequest : maintenanceRepository.findAll()) {
            maintenanceRequestDTOOuts.add(convertToDTO(maintenanceRequest));
        }
        return maintenanceRequestDTOOuts;
    }

    public MaintenanceRequestDTOOut getMaintenanceRequest(Integer id) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findMaintenanceRequestById(id);
        if (maintenanceRequest == null) {
            throw new ApiException("Maintenance request not found");
        }
        return convertToDTO(maintenanceRequest);
    }

    public void addMaintenanceRequest(MaintenanceRequestDTOIn maintenanceRequestDTOIn) {
        User user = userRepository.findUserById(maintenanceRequestDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(maintenanceRequestDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setUser(user);
        maintenanceRequest.setApartment(apartment);
        maintenanceRequest.setTitle(maintenanceRequestDTOIn.getTitle());
        maintenanceRequest.setDescription(maintenanceRequestDTOIn.getDescription());
        maintenanceRequest.setPriority(maintenanceRequestDTOIn.getPriority());
        maintenanceRepository.save(maintenanceRequest);
    }

    public void updateMaintenanceRequest(Integer id, MaintenanceRequestDTOIn maintenanceRequestDTOIn) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findMaintenanceRequestById(id);
        if (maintenanceRequest == null) {
            throw new ApiException("Maintenance request not found");
        }
        User user = userRepository.findUserById(maintenanceRequestDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(maintenanceRequestDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        maintenanceRequest.setUser(user);
        maintenanceRequest.setApartment(apartment);
        maintenanceRequest.setTitle(maintenanceRequestDTOIn.getTitle());
        maintenanceRequest.setDescription(maintenanceRequestDTOIn.getDescription());
        maintenanceRequest.setPriority(maintenanceRequestDTOIn.getPriority());
        maintenanceRepository.save(maintenanceRequest);
    }

    public void deleteMaintenanceRequest(Integer id) {
        MaintenanceRequest maintenanceRequest = maintenanceRepository.findMaintenanceRequestById(id);
        if (maintenanceRequest == null) {
            throw new ApiException("Maintenance request not found");
        }
        maintenanceRepository.deleteById(id);
    }

    public MaintenanceRequestDTOOut convertToDTO(MaintenanceRequest maintenanceRequest) {
        MaintenanceRequestDTOOut maintenanceRequestDTOOut = new MaintenanceRequestDTOOut();
        maintenanceRequestDTOOut.setId(maintenanceRequest.getId());
        maintenanceRequestDTOOut.setUserId(maintenanceRequest.getUser().getId());
        maintenanceRequestDTOOut.setApartmentId(maintenanceRequest.getApartment().getId());
        maintenanceRequestDTOOut.setTitle(maintenanceRequest.getTitle());
        maintenanceRequestDTOOut.setDescription(maintenanceRequest.getDescription());
        maintenanceRequestDTOOut.setStatus(maintenanceRequest.getStatus());
        maintenanceRequestDTOOut.setPriority(maintenanceRequest.getPriority());
        maintenanceRequestDTOOut.setCreatedAt(maintenanceRequest.getCreatedAt());
        maintenanceRequestDTOOut.setCompletedAt(maintenanceRequest.getCompletedAt());
        return maintenanceRequestDTOOut;
    }
}
