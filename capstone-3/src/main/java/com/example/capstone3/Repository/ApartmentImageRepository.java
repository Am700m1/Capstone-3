package com.example.capstone3.Repository;

import com.example.capstone3.Models.ApartmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentImageRepository extends JpaRepository<ApartmentImage, Integer> {

    ApartmentImage findApartmentImageById(Integer id);
}
