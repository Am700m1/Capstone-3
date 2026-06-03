package com.example.capstone3.Repository;

import com.example.capstone3.Models.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Apartment findApartmentById(Integer id);
}
