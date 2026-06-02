package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.BuildingDTOIn;
import com.example.capstone3.DTO.Out.BuildingDTOOut;
import com.example.capstone3.Models.Building;
import com.example.capstone3.Models.Owner;
import com.example.capstone3.Repository.BuildingRepository;
import com.example.capstone3.Repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final OwnerRepository ownerRepository;

    public List<BuildingDTOOut> getAll() {
        List<BuildingDTOOut> buildingDTOOuts = new ArrayList<>();
        for (Building building : buildingRepository.findAll()) {
            buildingDTOOuts.add(convertToDTO(building));
        }
        return buildingDTOOuts;
    }

    public BuildingDTOOut getBuilding(Integer id) {
        Building building = buildingRepository.findBuildingById(id);
        if (building == null) {
            throw new ApiException("Building not found");
        }
        return convertToDTO(building);
    }

    public void addBuilding(BuildingDTOIn buildingDTOIn) {
        Owner owner = ownerRepository.findOwnerById(buildingDTOIn.getOwnerId());
        if (owner == null) {
            throw new ApiException("Owner not found");
        }
        Building building = new Building();
        building.setOwner(owner);
        building.setName(buildingDTOIn.getName());
        building.setDistrict(buildingDTOIn.getDistrict());
        building.setAddress(buildingDTOIn.getAddress());
        building.setLatitude(buildingDTOIn.getLatitude());
        building.setLongitude(buildingDTOIn.getLongitude());
        buildingRepository.save(building);
    }

    public void updateBuilding(Integer id, BuildingDTOIn buildingDTOIn) {
        Building building = buildingRepository.findBuildingById(id);
        if (building == null) {
            throw new ApiException("Building not found");
        }
        Owner owner = ownerRepository.findOwnerById(buildingDTOIn.getOwnerId());
        if (owner == null) {
            throw new ApiException("Owner not found");
        }
        building.setOwner(owner);
        building.setName(buildingDTOIn.getName());
        building.setDistrict(buildingDTOIn.getDistrict());
        building.setAddress(buildingDTOIn.getAddress());
        building.setLatitude(buildingDTOIn.getLatitude());
        building.setLongitude(buildingDTOIn.getLongitude());
        buildingRepository.save(building);
    }

    public void deleteBuilding(Integer id) {
        Building building = buildingRepository.findBuildingById(id);
        if (building == null) {
            throw new ApiException("Building not found");
        }
        buildingRepository.deleteById(id);
    }

    public BuildingDTOOut convertToDTO(Building building) {
        BuildingDTOOut buildingDTOOut = new BuildingDTOOut();
        buildingDTOOut.setId(building.getId());
        buildingDTOOut.setOwnerId(building.getOwner().getId());
        buildingDTOOut.setOwnerName(building.getOwner().getFullName());
        buildingDTOOut.setName(building.getName());
        buildingDTOOut.setDistrict(building.getDistrict());
        buildingDTOOut.setAddress(building.getAddress());
        buildingDTOOut.setLatitude(building.getLatitude());
        buildingDTOOut.setLongitude(building.getLongitude());
        return buildingDTOOut;
    }
}
