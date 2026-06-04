package com.example.capstone3.Repository;

import com.example.capstone3.Models.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Apartment findApartmentById(Integer id);

    List<Apartment> findApartmentsByOwnerId(Integer ownerId);

    /** Returns all apartments with available = true (used by the recommendation engine). */
    List<Apartment> findByAvailableTrue();

    /** Returns available apartments filtered by district (case-sensitive). */
    List<Apartment> findByAvailableTrueAndBuilding_District(String district);

    @Query("SELECT a FROM Apartment a WHERE a.available = true " +
            "AND (:minRent IS NULL OR a.monthlyRent >= :minRent) " +
            "AND (:maxRent IS NULL OR a.monthlyRent <= :maxRent) " +
            "AND (:bedrooms IS NULL OR a.bedrooms = :bedrooms) " +
            "AND (:isFurnished IS NULL OR a.furnished = :isFurnished)"+
            "AND (:district IS NULL OR a.building.district = :district)")
    List<Apartment> searchAvailableApartments(
            @Param("minRent") Double minRent,
            @Param("maxRent") Double maxRent,
            @Param("bedrooms") Integer bedrooms,
            @Param("bedrooms") String district,
            @Param("isFurnished") Boolean isFurnished
    );
}
