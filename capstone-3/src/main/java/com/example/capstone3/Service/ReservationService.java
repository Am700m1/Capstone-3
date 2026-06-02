package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ReservationDTOIn;
import com.example.capstone3.DTO.Out.ReservationDTOOut;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.Reservation;
import com.example.capstone3.Models.User;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.ReservationRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    public List<ReservationDTOOut> getAll() {
        List<ReservationDTOOut> reservationDTOOuts = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAll()) {
            reservationDTOOuts.add(convertToDTO(reservation));
        }
        return reservationDTOOuts;
    }

    public ReservationDTOOut getReservation(Integer id) {
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        return convertToDTO(reservation);
    }

    public void addReservation(ReservationDTOIn reservationDTOIn) {
        Apartment apartment = apartmentRepository.findApartmentById(reservationDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        User user = userRepository.findUserById(reservationDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        Reservation reservation = new Reservation();
        reservation.setApartment(apartment);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDTOIn.getReservationDate());
        reservation.setStatus("PENDING");
        reservationRepository.save(reservation);
    }

    public void updateReservation(Integer id, ReservationDTOIn reservationDTOIn) {
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        Apartment apartment = apartmentRepository.findApartmentById(reservationDTOIn.getApartmentId());
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        User user = userRepository.findUserById(reservationDTOIn.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }
        reservation.setApartment(apartment);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDTOIn.getReservationDate());
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Integer id) {
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    public ReservationDTOOut convertToDTO(Reservation reservation) {
        ReservationDTOOut reservationDTOOut = new ReservationDTOOut();
        reservationDTOOut.setId(reservation.getId());
        reservationDTOOut.setApartmentId(reservation.getApartment().getId());
        reservationDTOOut.setUserId(reservation.getUser().getId());
        reservationDTOOut.setReservationDate(reservation.getReservationDate());
        reservationDTOOut.setStatus(reservation.getStatus());
        return reservationDTOOut;
    }
}
