package com.example.capstone3.Repository;

import com.example.capstone3.Enums.ReservationStatus;
import com.example.capstone3.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Reservation findReservationById(Integer id);

    List<Reservation> findReservationsByStatus(ReservationStatus status);

    List<Reservation> findReservationsByApartment_Owner_Id(Integer apartmentOwnerId);

    List<Reservation> findReservationsByUser_Id(Integer userId);

    List<Reservation> findByStatusAndReservationDateBefore(ReservationStatus status, LocalDate reservationDateBefore);
}
