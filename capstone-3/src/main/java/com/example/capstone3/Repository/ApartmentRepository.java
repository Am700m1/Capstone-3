package com.example.capstone3.Repository;

import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Enums.ApartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Apartment findApartmentById(Integer id);

    List<Apartment> findApartmentsByOwnerId(Integer ownerId);

    List<Apartment> findByStatus(ApartmentStatus status);

    List<Apartment> findByStatusAndBuilding_District(ApartmentStatus status, String district);

    @Query("SELECT a FROM Apartment a WHERE a.status = :status " +
            "AND (:minRent IS NULL OR a.monthlyRent >= :minRent) " +
            "AND (:maxRent IS NULL OR a.monthlyRent <= :maxRent) " +
            "AND (:bedrooms IS NULL OR a.bedrooms = :bedrooms) " +
            "AND (:isFurnished IS NULL OR a.furnished = :isFurnished) " +
            "AND (:district IS NULL OR a.building.district = :district)")
    List<Apartment> searchAvailableApartments(
            @Param("status") ApartmentStatus status,
            @Param("minRent") Double minRent,
            @Param("maxRent") Double maxRent,
            @Param("bedrooms") Integer bedrooms,
            @Param("district") String district,
            @Param("isFurnished") Boolean isFurnished
    );
}
