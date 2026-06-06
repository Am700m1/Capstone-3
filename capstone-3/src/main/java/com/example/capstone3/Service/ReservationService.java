package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ReservationDTOIn;
import com.example.capstone3.DTO.Out.ReservationDTOOut;
import com.example.capstone3.Enums.ApartmentStatus;
import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Enums.ReservationStatus;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.Contract;
import com.example.capstone3.Models.Owner;
import com.example.capstone3.Models.Reservation;
import com.example.capstone3.Models.User;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.ContractRepository;
import com.example.capstone3.Repository.OwnerRepository;
import com.example.capstone3.Repository.ReservationRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final ContractRepository contractRepository;

    public List<ReservationDTOOut> getAll() {
        List<ReservationDTOOut> reservationDTOOuts = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAll()) {
            reservationDTOOuts.add(convertToDTO(reservation));
        }
        return reservationDTOOuts;
    }

    public ReservationDTOOut getReservation(Integer id) { // add user id validation
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        return convertToDTO(reservation);
    }

    @Transactional
    public ReservationDTOOut addReservation(Integer userId, Integer apartmentId, ReservationDTOIn reservationDTOIn) {
        Apartment apartment = apartmentRepository.findApartmentById(apartmentId);
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (apartment.getStatus() != ApartmentStatus.AVAILABLE) {
            throw new ApiException("Apartment is not available for reservation");
        }
        if (apartment.getAvailableFrom() != null
                && reservationDTOIn.getReservationDate().isBefore(apartment.getAvailableFrom())) {
            throw new ApiException("Reservation date must be on or after the apartment available date");
        }
        if (reservationRepository.existsByApartment_IdAndStatus(apartment.getId(), ReservationStatus.APPROVED)) {
            throw new ApiException("Apartment already has an approved reservation");
        }
        if (contractRepository.existsByApartmentAndStatus(
                apartment.getId(), ContractStatus.ACTIVE)) {
            throw new ApiException("Apartment already has an active contract");
        }
        Reservation reservation = new Reservation();
        reservation.setApartment(apartment);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDTOIn.getReservationDate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setMessage(reservationDTOIn.getMessage());
        reservation.setCreatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    public void updateReservation(Integer id, ReservationDTOIn reservationDTOIn) {
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ApiException("Only pending reservations can be updated");
        }
        Apartment apartment = reservation.getApartment();
        if (apartment == null) {
            throw new ApiException("Apartment not found");
        }
        User user = reservation.getUser();
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (apartment.getAvailableFrom() != null
                && reservationDTOIn.getReservationDate().isBefore(apartment.getAvailableFrom())) {
            throw new ApiException("Reservation date must be on or after the apartment available date");
        }
        reservation.setApartment(apartment);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDTOIn.getReservationDate());
        reservation.setMessage(reservationDTOIn.getMessage());
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Integer id) {
        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ApiException("Only pending reservations can be deleted");
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
        reservationDTOOut.setMessage(reservation.getMessage());
        return reservationDTOOut;
    }


    //^^^^^^^CRUD^^^^^^^^


    public List<ReservationDTOOut> getPendingReservations(){
        List<Reservation> reservations = reservationRepository.findReservationsByStatus(ReservationStatus.PENDING);

        if(reservations.isEmpty()){
            throw new ApiException("No pending reservations were found!");
        }

        List<ReservationDTOOut> reservationDTOOuts = new ArrayList<>();

        for (Reservation reservation : reservations) {
            reservationDTOOuts.add(convertToDTO(reservation));
        }

        return reservationDTOOuts;
    }


    public List<ReservationDTOOut> getOwnerPendingReservations(Integer ownerId){
        Owner owner = ownerRepository.findOwnerById(ownerId);

        if (owner == null) {
            throw new ApiException("Owner not found");
        }

        List<Reservation> reservations = reservationRepository.findReservationsByStatusAndApartment_OwnerId(ReservationStatus.PENDING, ownerId);

        if(reservations.isEmpty()){
            throw new ApiException("No pending reservations for this owner were found!");
        }

        List<ReservationDTOOut> reservationDTOOuts = new ArrayList<>();

        for (Reservation reservation : reservations) {
            reservationDTOOuts.add(convertToDTO(reservation));
        }

        return reservationDTOOuts;
    }


    public List<ReservationDTOOut> getReservationsByUserId(Integer userId){
        User user = userRepository.findUserById(userId);

        if(user == null){
            throw new ApiException("User not found!");
        }

        List<Reservation> reservations = reservationRepository.findReservationsByUser_Id(userId);

        if(reservations.isEmpty()){
            throw new ApiException("No reservations for this user were found!");
        }

        List<ReservationDTOOut> reservationDTOOuts = new ArrayList<>();

        for (Reservation reservation : reservations) {
            reservationDTOOuts.add(convertToDTO(reservation));
        }

        return reservationDTOOuts;
    }


    @Transactional
    public void acceptReservation(Integer ownerId, Integer reservationId) {
        Reservation reservation = reservationRepository.findReservationById(reservationId);

        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }

        // Security Check: Does this owner own the apartment?
        if (!reservation.getApartment().getBuilding().getOwner().getId().equals(ownerId)) {
            throw new ApiException("You do not have permission to accept this reservation");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ApiException("Only pending reservations can be accepted");
        }

        Apartment apartment = reservation.getApartment();
        if (apartment.getStatus() != ApartmentStatus.AVAILABLE) {
            throw new ApiException("Apartment is no longer available");
        }
        if (contractRepository.existsByApartmentAndStatus(
                apartment.getId(), ContractStatus.ACTIVE)) {
            throw new ApiException("Apartment already has an active contract");
        }

        reservation.setStatus(ReservationStatus.APPROVED);
        apartment.setStatus(ApartmentStatus.RESERVED);

        for (Reservation competingReservation :
                reservationRepository.findReservationsByApartment_IdAndStatus(
                        apartment.getId(), ReservationStatus.PENDING)) {
            if (!competingReservation.getId().equals(reservationId)) {
                competingReservation.setStatus(ReservationStatus.REJECTED);
            }
        }

        reservationRepository.save(reservation);
        apartmentRepository.save(apartment);
    }


    @Transactional
    public void rejectReservation(Integer ownerId, Integer reservationId) {
        Reservation reservation = reservationRepository.findReservationById(reservationId);

        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }

        // Security Check
        if (!reservation.getApartment().getBuilding().getOwner().getId().equals(ownerId)) {
            throw new ApiException("You do not have permission to reject this reservation");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ApiException("Only pending reservations can be rejected");
        }

        cancelLinkedContract(reservation);
        reservation.setStatus(ReservationStatus.REJECTED);
        reservationRepository.save(reservation);
    }


    @Transactional
    public void endReservation(Integer userId, Integer reservationId) {
        Reservation reservation = reservationRepository.findReservationById(reservationId);

        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }

        // Security Check: Is this the user who made the reservation?
        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException("You can only cancel your own reservations");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new ApiException("Only pending or approved reservations can be cancelled");
        }

        Contract contract = contractRepository.findByReservation_Id(reservationId);
        if (contract != null && contract.getContractStatus() == ContractStatus.ACTIVE) {
            throw new ApiException("An active rental must be ended through the contract");
        }

        boolean wasApproved = reservation.getStatus() == ReservationStatus.APPROVED;
        cancelLinkedContract(reservation);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        if (wasApproved) {
            Apartment apartment = reservation.getApartment();
            apartment.setStatus(ApartmentStatus.AVAILABLE);
            apartmentRepository.save(apartment);
        }
    }

//    @Scheduled(fixedRate = 60000)
    @Scheduled(cron = "0 0 0 * * *") // Runs every day at midnight
    public void checkReservationExpiration() {

        // 1. Calculate the date from 2 days ago (48 hours)
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        // 2. Fetch all reservations that are still PENDING and were made before that date
        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, twoDaysAgo);

        // 3. Loop through them to update statuses
        for (Reservation reservation : expiredReservations) {

            // Mark the reservation as expired
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);

        }

        System.out.println("Ran expiration check: Expired " + expiredReservations.size() + " reservations.");
    }

    private void cancelLinkedContract(Reservation reservation) {
        Contract contract = contractRepository.findByReservation_Id(reservation.getId());
        if (contract != null && contract.getContractStatus() == ContractStatus.ACTIVE) {
            throw new ApiException("An active rental must be ended through the contract");
        }
        if (contract != null && contract.getContractStatus() == ContractStatus.PENDING) {
            contract.setSigned(false);
            contract.setContractStatus(ContractStatus.CANCELLED);
            contractRepository.save(contract);
        }
    }




}
