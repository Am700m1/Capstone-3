package com.example.capstone3.Repository;

import com.example.capstone3.Models.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Apartment findApartmentById(Integer id);

    /** Returns all apartments with available = true (used by the recommendation engine). */
    List<Apartment> findByAvailableTrue();

    /** Returns available apartments filtered by district (case-sensitive). */
    List<Apartment> findByAvailableTrueAndBuilding_District(String district);
}
