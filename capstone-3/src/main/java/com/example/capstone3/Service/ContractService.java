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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final OwnerRepository ownerRepository;

    private final AiService aiService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

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
            throw new ApiException("Cannot generate a contract! The reservation must be APPROVED by the owner first.");
        }
        if (contractRepository.existsByReservation_Id(reservation_id)) {
            throw new ApiException("A contract already exists for this reservation");
        }
        if (contractRepository.existsByApartmentAndStatus(
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
        Reservation reservation = contract.getReservation();
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        validateContractDates(contractDTOIn.getStartDate(), contractDTOIn.getEndDate());
        if (!reservation.getApartment().getMonthlyRent().equals(contractDTOIn.getMonthlyRent())) {
            throw new ApiException("Contract monthly rent must match the apartment monthly rent");
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

        User primaryTenant = contract.getReservation().getUser();
        contractDTOOut.setUserId(primaryTenant.getId());

        contractDTOOut.setContractNumber(contract.getContractNumber());
        contractDTOOut.setStartDate(contract.getStartDate());
        contractDTOOut.setEndDate(contract.getEndDate());
        contractDTOOut.setMonthlyRent(contract.getMonthlyRent());
        contractDTOOut.setSecurityDeposit(contract.getSecurityDeposit());
        contractDTOOut.setSigned(contract.getSigned());
        contractDTOOut.setSignedDate(contract.getSignedDate());
        contractDTOOut.setPdfPath(contract.getPdfPath());
        contractDTOOut.setContractStatus(contract.getContractStatus());

        // NEW: Joint Renting Logic
        if (primaryTenant.getCurrentRoommateId() != null) {
            User roommate = userRepository.findUserById(primaryTenant.getCurrentRoommateId());
            contractDTOOut.setIsJointContract(true);
            contractDTOOut.setCoTenantName(roommate != null ? roommate.getFullName() : "Unknown");
            contractDTOOut.setRentPerPerson(contract.getMonthlyRent() / 2); // Splits the rent
        } else {
            contractDTOOut.setIsJointContract(false);
            contractDTOOut.setCoTenantName(null);
            contractDTOOut.setRentPerPerson(contract.getMonthlyRent()); // Pays full amount
        }

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
        LocalDate today = LocalDate.now();
        if (today.isBefore(contract.getStartDate())) {
            throw new ApiException("Contract cannot be activated before its start date");
        }
        if (today.isAfter(contract.getEndDate())) {
            throw new ApiException("Contract cannot be activated after its end date");
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
        contract.setSignedDate(LocalDate.now());
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
        contract.setEndDate(LocalDate.now());
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

    public Map<String, Object> getContractAnalysis(Integer userId, Integer contractId, String language) {
        User user = userRepository.findUserById(userId);
        Contract contract = contractRepository.findContractById(contractId);

        if (user == null) {
            throw new ApiException("User not found!");
        }

        if (contract == null) {
            throw new ApiException("Contract not found!");
        }

        if (!userId.equals(contract.getReservation().getUser().getId())) {
            throw new ApiException("You are not authorized to view this contract's analysis.");
        }

        String normalizedLanguage = language == null ? "" : language.toUpperCase();
        if (!normalizedLanguage.equals("EN") && !normalizedLanguage.equals("AR")) {
            throw new ApiException("Language must be AR or EN");
        }

        try {
            String aiJsonString = aiService.analyzeContract(contract, normalizedLanguage);
            return objectMapper.readValue(aiJsonString, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});

        } catch (Exception e) {
            throw new ApiException("Failed to parse AI analysis: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processExpiredContracts() {
        LocalDate today = LocalDate.now();

        List<Contract> expiredContracts = contractRepository.findContractsByContractStatusAndEndDateBefore(ContractStatus.ACTIVE, today);

        if(expiredContracts.isEmpty()) {
            return;
        }

        for (Contract contract : expiredContracts) {

            contract.setContractStatus(ContractStatus.ENDED);
            contractRepository.save(contract);

            Reservation reservation = contract.getReservation();
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);

            Apartment apartment = reservation.getApartment();
            apartment.setStatus(ApartmentStatus.UNDER_MAINTENANCE);
            apartmentRepository.save(apartment);

            // 5. Trigger Notifications
            sendEndRentalNotificationToOwner(apartment.getOwner(), apartment);
            sendEndRentalNotificationToUser(reservation.getUser(), apartment);
        }

        System.out.println("Automated Check: Processed and closed " + expiredContracts.size() + " expired contracts.");
    }

    // --- NOTIFICATION HOLDING PLACES ---

    private void sendEndRentalNotificationToOwner(Owner owner, Apartment apartment) {
        // TODO: Implement Email / WhatsApp integration here in the future
        System.out.println(">> SMS/EMAIL TO OWNER (" + owner.getPhoneNumber() + " / " + owner.getEmail() + "): " +
                "The contract for apartment '" + apartment.getTitle() + "' has ended. " +
                "The system has automatically placed the apartment UNDER_MAINTENANCE for your inspection.");
    }

    private void sendEndRentalNotificationToUser(User user, Apartment apartment) {
        // TODO: Implement Email / WhatsApp integration here in the future
        System.out.println(">> SMS/EMAIL TO TENANT (" + user.getPhoneNumber() + " / " + user.getEmail() + "): " +
                "Dear " + user.getFullName() + ", your rental contract for '" + apartment.getTitle() + "' has officially ended today. " +
                "Please make sure to hand over the apartment and submit any final reviews.");
    }

    private void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new ApiException("Contract end date must be after start date");
        }
    }

}
