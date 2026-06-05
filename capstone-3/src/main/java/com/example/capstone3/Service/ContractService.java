package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ContractDTOIn;
import com.example.capstone3.DTO.Out.ContractDTOOut;
import com.example.capstone3.Enums.ApartmentStatus;
import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Enums.ReservationStatus;
import com.example.capstone3.Models.*;
import com.example.capstone3.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final OwnerRepository ownerRepository;

    public List<ContractDTOOut> getAll() {
        List<ContractDTOOut> contractDTOOuts = new ArrayList<>();
        for (Contract contract : contractRepository.findAll()) {
            contractDTOOuts.add(convertToDTO(contract));
        }
        return contractDTOOuts;
    }

    public ContractDTOOut getContract(Integer id) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }
        return convertToDTO(contract);
    }

    @Transactional
    public void addContract(ContractDTOIn contractDTOIn, Integer reservation_id) {
        Reservation reservation = reservationRepository.findReservationById(reservation_id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new ApiException("Contract can only be created for an approved reservation");
        }
        if (contractRepository.existsByReservation_Id(reservation_id)) {
            throw new ApiException("A contract already exists for this reservation");
        }
        if (contractRepository.existsByReservation_Apartment_IdAndContractStatus(
                reservation.getApartment().getId(), ContractStatus.ACTIVE)) {
            throw new ApiException("Apartment already has an active contract");
        }
        validateContractDates(contractDTOIn.getStartDate(), contractDTOIn.getEndDate());
        if (!reservation.getApartment().getMonthlyRent().equals(contractDTOIn.getMonthlyRent())) {
            throw new ApiException("Contract monthly rent must match the apartment monthly rent");
        }
        Contract contract = new Contract();
        contract.setReservation(reservation);
        contract.setContractNumber(contractDTOIn.getContractNumber());
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setMonthlyRent(contractDTOIn.getMonthlyRent());
        contract.setSecurityDeposit(contractDTOIn.getSecurityDeposit());
        contract.setSigned(false);
        contract.setSignedDate(null);
        contract.setPdfPath(contractDTOIn.getPdfPath());
        contract.setContractStatus(ContractStatus.PENDING);
        contractRepository.save(contract);
    }

    public void updateContract(Integer id, ContractDTOIn contractDTOIn) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }
        if (contract.getContractStatus() != ContractStatus.PENDING) {
            throw new ApiException("Only pending contracts can be updated");
        }
        validateContractDates(contractDTOIn.getStartDate(), contractDTOIn.getEndDate());
        if (!contract.getReservation().getApartment().getMonthlyRent().equals(contractDTOIn.getMonthlyRent())) {
            throw new ApiException("Contract monthly rent must match the apartment monthly rent");
        }
        Reservation reservation = contract.getReservation();
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        contract.setReservation(reservation);
        contract.setContractNumber(contractDTOIn.getContractNumber());
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setMonthlyRent(contractDTOIn.getMonthlyRent());
        contract.setSecurityDeposit(contractDTOIn.getSecurityDeposit());
        contract.setPdfPath(contractDTOIn.getPdfPath());
        contractRepository.save(contract);
    }

    public void deleteContract(Integer id) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }
        if (contract.getContractStatus() != ContractStatus.PENDING
                && contract.getContractStatus() != ContractStatus.CANCELLED) {
            throw new ApiException("Only pending or cancelled contracts can be deleted");
        }
        contractRepository.deleteById(id);
    }

    public ContractDTOOut convertToDTO(Contract contract) {
        ContractDTOOut contractDTOOut = new ContractDTOOut();
        contractDTOOut.setId(contract.getId());
        contractDTOOut.setReservationId(contract.getReservation().getId());
        contractDTOOut.setApartmentId(contract.getReservation().getApartment().getId());
        contractDTOOut.setUserId(contract.getReservation().getUser().getId());
        contractDTOOut.setContractNumber(contract.getContractNumber());
        contractDTOOut.setStartDate(contract.getStartDate());
        contractDTOOut.setEndDate(contract.getEndDate());
        contractDTOOut.setMonthlyRent(contract.getMonthlyRent());
        contractDTOOut.setSecurityDeposit(contract.getSecurityDeposit());
        contractDTOOut.setSigned(contract.getSigned());
        contractDTOOut.setSignedDate(contract.getSignedDate());
        contractDTOOut.setPdfPath(contract.getPdfPath());
        contractDTOOut.setContractStatus(contract.getContractStatus());
        return contractDTOOut;
    }


    //^^^^^^^CRUD^^^^^^^^


    @Transactional
    public void acceptContract(Integer userId, Integer contractId){
        User user = userRepository.findUserById(userId);
        Contract contract = contractRepository.findContractById(contractId);

        if(user == null){
            throw new ApiException("User not found!");
        }

        if (contract == null) {
            throw new ApiException("Contract was not found!");
        }

        if(!userId.equals(contract.getReservation().getUser().getId())){
            throw new ApiException("You are not authorized to do this action");
        }
        if (contract.getContractStatus() != ContractStatus.PENDING) {
            throw new ApiException("Only pending contracts can be accepted");
        }

        Reservation reservation = contract.getReservation();
        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new ApiException("Reservation must be approved before accepting the contract");
        }
        Apartment apartment = reservation.getApartment();
        if (apartment.getStatus() != ApartmentStatus.RESERVED) {
            throw new ApiException("Apartment must be reserved before accepting the contract");
        }

        contract.setSigned(true);
        contract.setSignedDate(contract.getSignedDate() == null ? LocalDate.now() : contract.getSignedDate());
        contract.setContractStatus(ContractStatus.ACTIVE);
        contractRepository.save(contract);

        apartment.setStatus(ApartmentStatus.RENTED);
        apartmentRepository.save(apartment);
    }


    @Transactional
    public void rejectContract(Integer userId, Integer contractId){
        User user = userRepository.findUserById(userId);
        Contract contract = contractRepository.findContractById(contractId);

        if(user == null){
            throw new ApiException("User not found!");
        }

        if (contract == null) {
            throw new ApiException("Contract was not found!");
        }

        if(!userId.equals(contract.getReservation().getUser().getId())){
            throw new ApiException("You are not authorized to do this action");
        }
        if (contract.getContractStatus() != ContractStatus.PENDING) {
            throw new ApiException("Only pending contracts can be rejected");
        }

        Reservation reservation = contract.getReservation();

        contract.setSigned(false);
        contract.setContractStatus(ContractStatus.CANCELLED);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        contractRepository.save(contract);

        Apartment apartment = apartmentRepository.findApartmentById(contract.getReservation().getApartment().getId());
        apartment.setStatus(ApartmentStatus.AVAILABLE);
        apartmentRepository.save(apartment);
    }



    public List<ContractDTOOut> getContractsByUserId(Integer userId){
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found!");
        }

        List<Contract> contracts = contractRepository.findContractsByReservation_User_Id(userId);

        if (contracts.isEmpty()) {
            throw new ApiException("No contracts were found for this user!");
        }

        List<ContractDTOOut> contractDTOOuts = new ArrayList<>();

        for(Contract contract: contracts){
            contractDTOOuts.add(convertToDTO(contract));
        }

        return contractDTOOuts;
    }


    public List<ContractDTOOut> getContractsByOwnerId(Integer ownerId){
        Owner owner = ownerRepository.findOwnerById(ownerId);

        if(owner == null){
            throw new ApiException("Owner not found!");
        }

        List<Contract> contracts = contractRepository.findContractsByReservation_Apartment_Owner_Id(ownerId);

        if (contracts.isEmpty()) {
            throw new ApiException("No contracts for this user were found!");
        }

        List<ContractDTOOut> contractDTOOuts = new ArrayList<>();

        for(Contract contract: contracts){
            contractDTOOuts.add(convertToDTO(contract));
        }

        return contractDTOOuts;
    }


    @Transactional
    public void endContract(Integer ownerId, Integer contractId){
        Owner owner = ownerRepository.findOwnerById(ownerId);
        Contract contract = contractRepository.findContractById(contractId);

        if(owner == null){
            throw new ApiException("Owner not found!");
        }

        if (contract == null) {
            throw new ApiException("Contract not found!");
        }

        if(!contract.getReservation().getApartment().getOwner().getId().equals(ownerId)){
            throw new ApiException("You are not authorized to do this action");
        }

        if(!contract.getContractStatus().equals(ContractStatus.ACTIVE)){
            throw new ApiException("Contract is not active!");
        }

        contract.setContractStatus(ContractStatus.ENDED);
        contractRepository.save(contract);

        Reservation reservation = contract.getReservation();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        Apartment apartment = contract.getReservation().getApartment();
        apartment.setStatus(ApartmentStatus.UNDER_MAINTENANCE);
        apartmentRepository.save(apartment);
    }


    @Transactional
    public void renewContract(Integer userId, Integer contractId, Integer extraMonths) {
        User user = userRepository.findUserById(userId);
        Contract contract = contractRepository.findContractById(contractId);

        if (user == null) {
            throw new ApiException("User not found!");
        }
        if (contract == null) {
            throw new ApiException("Contract was not found!");
        }

        // 1. Check if the user is the one on the contract
        if (!userId.equals(contract.getReservation().getUser().getId())) {
            throw new ApiException("You are not authorized to renew this contract.");
        }

        // 2. Check if the contract is currently ACTIVE
        if (!ContractStatus.ACTIVE.equals(contract.getContractStatus())) {
            throw new ApiException("Only active contracts can be renewed.");
        }

        if (extraMonths <= 0) {
            throw new ApiException("Extra months must be greater than zero.");
        }

        // 3. Update the endDate of the contract by adding the extraMonths
        LocalDate currentEndDate = contract.getEndDate();
        LocalDate newEndDate = currentEndDate.plusMonths(extraMonths);

        contract.setEndDate(newEndDate);
        contractRepository.save(contract);
    }

    private void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new ApiException("Contract end date must be after start date");
        }
    }

}
