package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.UserPreferenceDTOIn;
import com.example.capstone3.DTO.Out.UserPreferenceDTOOut;
import com.example.capstone3.Enums.PreferenceLevel;
import com.example.capstone3.Models.User;
import com.example.capstone3.Models.UserPreference;
import com.example.capstone3.Repository.UserPreferenceRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    public List<UserPreferenceDTOOut> getAll() {
        List<UserPreferenceDTOOut> userPreferenceDTOOuts = new ArrayList<>();
        for (UserPreference preference : userPreferenceRepository.findAll()) {
            userPreferenceDTOOuts.add(convertToDTO(preference));
        }
        return userPreferenceDTOOuts;
    }

    public UserPreferenceDTOOut getUserPreference(Integer id) {
        UserPreference preference = userPreferenceRepository.findUserPreferenceById(id);
        if (preference == null) {
            throw new ApiException("User preference not found");
        }
        return convertToDTO(preference);
    }

    public void addUserPreference(UserPreferenceDTOIn userPreferenceDTOIn) {
        User user = userRepository.findUserById(userPreferenceDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setWorkLatitude(userPreferenceDTOIn.getWorkLatitude());
        preference.setWorkLongitude(userPreferenceDTOIn.getWorkLongitude());
        preference.setMaxBudget(userPreferenceDTOIn.getMaxBudget());
        preference.setMaxCommuteMinutes(userPreferenceDTOIn.getMaxCommuteMinutes());
        preference.setRequiresParking(userPreferenceDTOIn.getRequiresParking());
        preference.setRequiresElevator(userPreferenceDTOIn.getRequiresElevator());
        preference.setRequiresFurnished(userPreferenceDTOIn.getRequiresFurnished());
        preference.setGymPreference(parsePreferenceLevel(userPreferenceDTOIn.getGymPreference()));
        preference.setCafesPreference(parsePreferenceLevel(userPreferenceDTOIn.getCafesPreference()));
        preference.setHospitalPreference(parsePreferenceLevel(userPreferenceDTOIn.getHospitalPreference()));
        preference.setSchoolPreference(parsePreferenceLevel(userPreferenceDTOIn.getSchoolPreference()));
        preference.setPublicTransportPreference(parsePreferenceLevel(userPreferenceDTOIn.getPublicTransportPreference()));
        preference.setPreferredBedrooms(userPreferenceDTOIn.getPreferredBedrooms());
        preference.setPreferredBathrooms(userPreferenceDTOIn.getPreferredBathrooms());
        preference.setPreferredDistrict(userPreferenceDTOIn.getPreferredDistrict());
        userPreferenceRepository.save(preference);
    }

    public void updateUserPreference(Integer id, UserPreferenceDTOIn userPreferenceDTOIn) {
        UserPreference preference = userPreferenceRepository.findUserPreferenceById(id);
        if (preference == null) {
            throw new ApiException("User preference not found");
        }
        User user = userRepository.findUserById(userPreferenceDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        preference.setUser(user);
        preference.setWorkLatitude(userPreferenceDTOIn.getWorkLatitude());
        preference.setWorkLongitude(userPreferenceDTOIn.getWorkLongitude());
        preference.setMaxBudget(userPreferenceDTOIn.getMaxBudget());
        preference.setMaxCommuteMinutes(userPreferenceDTOIn.getMaxCommuteMinutes());
        preference.setRequiresParking(userPreferenceDTOIn.getRequiresParking());
        preference.setRequiresElevator(userPreferenceDTOIn.getRequiresElevator());
        preference.setRequiresFurnished(userPreferenceDTOIn.getRequiresFurnished());
        preference.setGymPreference(parsePreferenceLevel(userPreferenceDTOIn.getGymPreference()));
        preference.setCafesPreference(parsePreferenceLevel(userPreferenceDTOIn.getCafesPreference()));
        preference.setHospitalPreference(parsePreferenceLevel(userPreferenceDTOIn.getHospitalPreference()));
        preference.setSchoolPreference(parsePreferenceLevel(userPreferenceDTOIn.getSchoolPreference()));
        preference.setPublicTransportPreference(parsePreferenceLevel(userPreferenceDTOIn.getPublicTransportPreference()));
        preference.setPreferredBedrooms(userPreferenceDTOIn.getPreferredBedrooms());
        preference.setPreferredBathrooms(userPreferenceDTOIn.getPreferredBathrooms());
        preference.setPreferredDistrict(userPreferenceDTOIn.getPreferredDistrict());
        userPreferenceRepository.save(preference);
    }

    public void deleteUserPreference(Integer id) {
        UserPreference preference = userPreferenceRepository.findUserPreferenceById(id);
        if (preference == null) {
            throw new ApiException("User preference not found");
        }
        userPreferenceRepository.deleteById(id);
    }

    public UserPreferenceDTOOut convertToDTO(UserPreference preference) {
        UserPreferenceDTOOut userPreferenceDTOOut = new UserPreferenceDTOOut();
        userPreferenceDTOOut.setId(preference.getId());
        userPreferenceDTOOut.setUserId(preference.getUser().getId());
        userPreferenceDTOOut.setWorkLatitude(preference.getWorkLatitude());
        userPreferenceDTOOut.setWorkLongitude(preference.getWorkLongitude());
        userPreferenceDTOOut.setMaxBudget(preference.getMaxBudget());
        userPreferenceDTOOut.setMaxCommuteMinutes(preference.getMaxCommuteMinutes());
        userPreferenceDTOOut.setRequiresParking(preference.getRequiresParking());
        userPreferenceDTOOut.setRequiresElevator(preference.getRequiresElevator());
        userPreferenceDTOOut.setRequiresFurnished(preference.getRequiresFurnished());
        userPreferenceDTOOut.setGymPreference(preference.getGymPreference() == null ? null : preference.getGymPreference().name());
        userPreferenceDTOOut.setCafesPreference(preference.getCafesPreference() == null ? null : preference.getCafesPreference().name());
        userPreferenceDTOOut.setHospitalPreference(preference.getHospitalPreference() == null ? null : preference.getHospitalPreference().name());
        userPreferenceDTOOut.setSchoolPreference(preference.getSchoolPreference() == null ? null : preference.getSchoolPreference().name());
        userPreferenceDTOOut.setPublicTransportPreference(preference.getPublicTransportPreference() == null ? null : preference.getPublicTransportPreference().name());
        userPreferenceDTOOut.setPreferredBedrooms(preference.getPreferredBedrooms());
        userPreferenceDTOOut.setPreferredBathrooms(preference.getPreferredBathrooms());
        userPreferenceDTOOut.setPreferredDistrict(preference.getPreferredDistrict());
        return userPreferenceDTOOut;
    }


    //^^^^^^^CRUD^^^^^^^^


    private PreferenceLevel parsePreferenceLevel(String value) {
        if (value == null || value.isBlank()) {
            return PreferenceLevel.NOT_IMPORTANT;
        }
        return PreferenceLevel.valueOf(value.toUpperCase());
    }
}
