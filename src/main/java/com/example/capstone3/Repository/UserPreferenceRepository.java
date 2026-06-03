package com.example.capstone3.Repository;

import com.example.capstone3.Models.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {

    UserPreference findUserPreferenceById(Integer id);
}
