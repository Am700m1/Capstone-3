package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ContractDTOIn;
import com.example.capstone3.DTO.Out.ContractDTOOut;
import com.example.capstone3.Enums.ApartmentStatus;
import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Enums.ReservationStatus;
import com.example.capstone3.Models.*;
import com.example.capstone3.Repository.*;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final OwnerRepository ownerRepository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

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
        validateContractStartAgainstReservation(
                contractDTOIn.getStartDate(), reservation.getReservationDate());
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

        whatsAppService.notifyTenantContractCreated(reservation.getUser(), contract);
    }

    public void updateContract(Integer id, ContractDTOIn contractDTOIn) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }
        if (contract.getContractStatus() != ContractStatus.PENDING) {
            throw new ApiException("Only pending contracts can be updated");
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
        validateContractDates(contractDTOIn.getStartDate(), contractDTOIn.getEndDate());
        validateContractStartAgainstReservation(
                contractDTOIn.getStartDate(), reservation.getReservationDate());
        if (!reservation.getApartment().getMonthlyRent().equals(contractDTOIn.getMonthlyRent())) {
            throw new ApiException("Contract monthly rent must match the apartment monthly rent");
        }
        contract.setReservation(reservation);
        contract.setContractNumber(contractDTOIn.getContractNumber());
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setMonthlyRent(contractDTOIn.getMonthlyRent());
        contract.setSecurityDeposit(contractDTOIn.getSecurityDeposit());
        contract.setSigned(contractDTOIn.getSigned());
        contract.setSignedDate(contractDTOIn.getSignedDate());
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
            if (roommate == null || !primaryTenant.getId().equals(roommate.getCurrentRoommateId())) {
                throw new ApiException("Roommate relationship is invalid");
            }
            contractDTOOut.setIsJointContract(true);
            contractDTOOut.setCoTenantName(roommate.getFullName());
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
        contract.setSignedDate(LocalDate.now());
        contract.setSignedDate(contract.getSignedDate() == null ? LocalDate.now() : contract.getSignedDate());
        contract.setContractStatus(ContractStatus.ACTIVE);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
        contractRepository.save(contract);


        Apartment apartment = apartmentRepository.findApartmentById(contract.getReservation().getApartment().getId());
        apartment.setStatus(ApartmentStatus.RENTED);
        apartmentRepository.save(apartment);

        whatsAppService.notifyOwnerContractAccepted(apartment.getOwner(), apartment, user);
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

        whatsAppService.notifyOwnerContractRejected(apartment.getOwner(), apartment, user);
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
        apartment.setAvailable(false);
        apartmentRepository.save(apartment);

        whatsAppService.notifyOwnerContractEnded(apartment.getOwner(), apartment);
        whatsAppService.notifyTenantContractEnded(reservation.getUser(), apartment);
    }

    @Transactional
    public void terminateContract(Integer ownerId, Integer contractId) {
        Owner owner = ownerRepository.findOwnerById(ownerId);
        if (owner == null) {
            throw new ApiException("Owner not found!");
        }

        Contract contract = contractRepository.findContractById(contractId);
        if (contract == null) {
            throw new ApiException("Contract not found!");
        }
        if (!contract.getReservation().getApartment().getOwner().getId().equals(ownerId)) {
            throw new ApiException("You are not authorized to terminate this contract");
        }
        if (contract.getContractStatus() != ContractStatus.ACTIVE) {
            throw new ApiException("Only active contracts can be terminated");
        }

        contract.setContractStatus(ContractStatus.TERMINATED);
        contract.setEndDate(LocalDate.now());
        contractRepository.save(contract);

        Reservation reservation = contract.getReservation();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        Apartment apartment = reservation.getApartment();
        apartment.setStatus(ApartmentStatus.UNDER_MAINTENANCE);
        apartmentRepository.save(apartment);

        whatsAppService.notifyTenantContractTerminated(reservation.getUser(), apartment);
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

        whatsAppService.notifyTenantContractRenewed(user, contract);
        whatsAppService.notifyOwnerContractRenewed(
                contract.getReservation().getApartment().getOwner(), contract);
    }

    private void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new ApiException("Contract end date must be after start date");
        }
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

        String normalizedLanguage = language == null || language.isBlank()
                ? "EN" : language.trim().toUpperCase();
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
            apartment.setAvailable(false);
            apartmentRepository.save(apartment);

            // 5. Trigger Notifications
            whatsAppService.notifyOwnerContractEnded(apartment.getOwner(), apartment);
            whatsAppService.notifyTenantContractEnded(reservation.getUser(), apartment);
        }

        System.out.println("Automated Check: Processed and closed " + expiredContracts.size() + " expired contracts.");
    }

    // --- NOTIFICATION HOLDING PLACES ---


    public void generateAndEmailContractPdf(Integer contractId) throws IOException, MessagingException {
        Contract contract = contractRepository.findContractById(contractId);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }

        String tenantName     = contract.getReservation().getUser().getFullName();
        String tenantEmail    = contract.getReservation().getUser().getEmail();
        String tenantPhone    = contract.getReservation().getUser().getPhoneNumber();
        String apartmentTitle = contract.getReservation().getApartment().getTitle();
        String contractNum    = contract.getContractNumber();
        String monthlyRent    = String.valueOf(contract.getMonthlyRent());
        String ownerName      = contract.getReservation().getApartment().getBuilding().getOwner().getFullName();
        String ownerEmail     = contract.getReservation().getApartment().getBuilding().getOwner().getEmail();
        String ownerPhone     = contract.getReservation().getApartment().getBuilding().getOwner().getPhoneNumber();
        String buildingName   = contract.getReservation().getApartment().getBuilding().getName();
        String district       = contract.getReservation().getApartment().getBuilding().getDistrict();
        String startDate      = contract.getStartDate().toString();
        String endDate        = contract.getEndDate().toString();
        String secDeposit     = contract.getSecurityDeposit() != null ? String.valueOf(contract.getSecurityDeposit()) : "N/A";
        String signedDate     = contract.getSignedDate() != null ? contract.getSignedDate().toString() : "Not signed yet";
        String status         = contract.getContractStatus().toString();
        String signed         = Boolean.TRUE.equals(contract.getSigned()) ? "Yes" : "No";
        String reservationMsg = contract.getReservation().getMessage() != null ? contract.getReservation().getMessage() : "N/A";

        Apartment apt         = contract.getReservation().getApartment();
        String furnished      = Boolean.TRUE.equals(apt.getFurnished()) ? "Yes" : "No";
        String waterIncluded  = Boolean.TRUE.equals(apt.getWaterIncluded()) ? "Included" : "Not included";
        String internetIncluded   = Boolean.TRUE.equals(apt.getInternetIncluded()) ? "Included" : "Not included";
        String electricityIncluded = Boolean.TRUE.equals(apt.getElectricityIncluded()) ? "Included" : "Not included";
        String allowedTenantType  = apt.getAllowedTenantType() != null ? apt.getAllowedTenantType() : "N/A";
        String floorNumber        = apt.getFloorNumber() != null ? String.valueOf(apt.getFloorNumber()) : "N/A";

        Building building     = contract.getReservation().getApartment().getBuilding();
        String hasParking     = Boolean.TRUE.equals(building.getHasParking()) ? "Yes" : "No";
        String hasElevator    = Boolean.TRUE.equals(building.getHasElevator()) ? "Yes" : "No";
        String hasSecurity    = Boolean.TRUE.equals(building.getHasSecurity()) ? "Yes" : "No";
        String petsAllowed    = Boolean.TRUE.equals(building.getPetsAllowed()) ? "Yes" : "No";

        String pdfHtml = "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'/>" +
                "<style>" +
                "@page { size: A4; margin: 22px; }" +
                "* { box-sizing: border-box; }" +
                "body { font-family: Arial, sans-serif; background: #f0fafa; color: #1a2e2c; margin: 0; font-size: 10px; }" +
                ".container { width: 100%; }" +
                ".header { text-align: center; margin-bottom: 12px; }" +
                ".brand-mark { color: #0F766E; font-size: 18px; font-weight: bold; margin-bottom: 2px; }" +
                ".brand-name { color: #0F766E; font-size: 21px; font-weight: bold; letter-spacing: 2px; }" +
                ".brand-sub { color: #718096; font-size: 8px; letter-spacing: 1px; margin: 2px 0 9px; }" +
                ".document-title { font-size: 17px; font-weight: bold; margin: 0 0 5px; }" +
                ".document-meta { color: #718096; font-size: 9px; }" +
                ".document-meta strong { color: #1a2e2c; }" +
                ".status { color: #0F766E; font-weight: bold; }" +
                ".divider { border-top: 2px solid #0F766E; margin-bottom: 12px; }" +
                ".layout { width: 100%; border-collapse: separate; border-spacing: 0; margin-bottom: 10px; }" +
                ".layout-cell { width: 50%; vertical-align: top; }" +
                ".layout-left { padding-right: 5px; }" +
                ".layout-right { padding-left: 5px; }" +
                ".card { width: 100%; border: 1px solid #d1e8e6; border-collapse: collapse; background: #ffffff; margin-bottom: 10px; }" +
                ".card-title { background: #0F766E; color: #ffffff; font-size: 10px; font-weight: bold; letter-spacing: 0.4px; padding: 7px 10px; }" +
                ".card td { border-bottom: 1px solid #e8f5f4; padding: 6px 10px; vertical-align: top; }" +
                ".card tr:last-child td { border-bottom: 0; }" +
                ".label { color: #718096; width: 48%; }" +
                ".value { color: #1a2e2c; }" +
                ".positive { color: #0F766E; font-weight: bold; }" +
                ".negative { color: #e24b4a; }" +
                ".person { padding: 9px 10px; border-bottom: 1px solid #e8f5f4; }" +
                ".person-name { font-size: 11px; font-weight: bold; }" +
                ".person-role { color: #718096; font-size: 9px; margin-top: 2px; }" +
                ".terms { width: 100%; border-collapse: collapse; }" +
                ".terms td { width: 25%; border-right: 1px solid #e8f5f4; border-bottom: 1px solid #e8f5f4; padding: 7px 10px; }" +
                ".terms td:last-child { border-right: 0; }" +
                ".notes-label { color: #718096; width: 18%; }" +
                ".notes-value { color: #718096; font-style: italic; }" +
                ".footer { border: 1px solid #d1e8e6; background: #f0fafa; color: #718096; padding: 9px 12px; font-size: 9px; line-height: 1.5; }" +
                "</style></head><body><div class='container'>" +

                "<div class='header'>" +
                "<div class='brand-mark'>&#9632;</div>" +
                "<div class='brand-name'>RAWAA</div>" +
                "<div class='brand-sub'>SMART RENTAL PLATFORM</div>" +
                "<h1 class='document-title'>Official lease agreement</h1>" +
                "<div class='document-meta'>Contract no: <strong>" + contractNum + "</strong> &#160; | &#160; Status: <span class='status'>" + status + "</span></div>" +
                "</div>" +
                "<div class='divider'></div>" +

                "<table class='layout'><tr>" +
                "<td class='layout-cell layout-left'>" +
                "<table class='card'>" +
                "<tr><td class='card-title' colspan='2'>PROPERTY INFORMATION</td></tr>" +
                "<tr><td class='label'>Building name</td><td class='value'>" + buildingName + "</td></tr>" +
                "<tr><td class='label'>Apartment</td><td class='value'>" + apartmentTitle + "</td></tr>" +
                "<tr><td class='label'>District</td><td class='value'>" + district + "</td></tr>" +
                "<tr><td class='label'>Floor number</td><td class='value'>" + floorNumber + "</td></tr>" +
                "<tr><td class='label'>Allowed tenant type</td><td class='value'>" + allowedTenantType + "</td></tr>" +
                "</table>" +
                "</td>" +
                "<td class='layout-cell layout-right'>" +
                "<table class='card'>" +
                "<tr><td class='card-title' colspan='2'>BUILDING FACILITIES</td></tr>" +
                "<tr><td class='label'>Parking</td><td class='" + ("Yes".equals(hasParking) ? "positive" : "negative") + "'>" + hasParking + "</td></tr>" +
                "<tr><td class='label'>Elevator</td><td class='" + ("Yes".equals(hasElevator) ? "positive" : "negative") + "'>" + hasElevator + "</td></tr>" +
                "<tr><td class='label'>Security</td><td class='" + ("Yes".equals(hasSecurity) ? "positive" : "negative") + "'>" + hasSecurity + "</td></tr>" +
                "<tr><td class='label'>Pets allowed</td><td class='" + ("Yes".equals(petsAllowed) ? "positive" : "negative") + "'>" + petsAllowed + "</td></tr>" +
                "</table>" +
                "<table class='card'>" +
                "<tr><td class='card-title' colspan='2'>APARTMENT INCLUSIONS</td></tr>" +
                "<tr><td class='label'>Furnished</td><td class='" + ("Yes".equals(furnished) ? "positive" : "negative") + "'>" + furnished + "</td></tr>" +
                "<tr><td class='label'>Water</td><td class='" + ("Included".equals(waterIncluded) ? "positive" : "negative") + "'>" + waterIncluded + "</td></tr>" +
                "<tr><td class='label'>Internet</td><td class='" + ("Included".equals(internetIncluded) ? "positive" : "negative") + "'>" + internetIncluded + "</td></tr>" +
                "<tr><td class='label'>Electricity</td><td class='" + ("Included".equals(electricityIncluded) ? "positive" : "negative") + "'>" + electricityIncluded + "</td></tr>" +
                "</table>" +
                "</td></tr></table>" +

                "<table class='layout'><tr>" +
                "<td class='layout-cell layout-left'>" +
                "<table class='card'>" +
                "<tr><td class='card-title' colspan='2'>OWNER INFORMATION</td></tr>" +
                "<tr><td class='person' colspan='2'><div class='person-name'>" + ownerName + "</div><div class='person-role'>Property Owner</div></td></tr>" +
                "<tr><td class='label'>Email</td><td class='value'>" + ownerEmail + "</td></tr>" +
                "<tr><td class='label'>Phone</td><td class='value'>" + ownerPhone + "</td></tr>" +
                "</table>" +
                "</td>" +
                "<td class='layout-cell layout-right'>" +
                "<table class='card'>" +
                "<tr><td class='card-title' colspan='2'>TENANT INFORMATION</td></tr>" +
                "<tr><td class='person' colspan='2'><div class='person-name'>" + tenantName + "</div><div class='person-role'>Tenant</div></td></tr>" +
                "<tr><td class='label'>Email</td><td class='value'>" + tenantEmail + "</td></tr>" +
                "<tr><td class='label'>Phone</td><td class='value'>" + tenantPhone + "</td></tr>" +
                "</table>" +
                "</td></tr></table>" +

                "<table class='card'>" +
                "<tr><td class='card-title' colspan='4'>CONTRACT TERMS</td></tr>" +
                "<tr class='terms'>" +
                "<td class='label'>Start date</td><td class='value'>" + startDate + "</td>" +
                "<td class='label'>End date</td><td class='value'>" + endDate + "</td>" +
                "</tr>" +
                "<tr class='terms'>" +
                "<td class='label'>Monthly rent</td><td class='positive'>" + monthlyRent + " SAR</td>" +
                "<td class='label'>Security deposit</td><td class='value'>" + secDeposit + ("N/A".equals(secDeposit) ? "" : " SAR") + "</td>" +
                "</tr>" +
                "<tr class='terms'>" +
                "<td class='label'>Signed</td><td class='" + ("Yes".equals(signed) ? "positive" : "negative") + "'>" + signed + "</td>" +
                "<td class='label'>Signed date</td><td class='value'>" + signedDate + "</td>" +
                "</tr>" +
                "<tr><td class='notes-label'>Tenant notes</td><td class='notes-value' colspan='3'>" + reservationMsg + "</td></tr>" +
                "</table>" +

                "<div class='footer'>This document is an official lease agreement generated by Rawaa - Smart Rental Platform. " +
                "Both parties are bound by the terms stated above.</div>" +
                "</div></body></html>";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(pdfHtml, "/");
        builder.toStream(os);
        builder.run();

        emailService.sendEmailWithPdf(
                tenantEmail,
                "Lease Contract Attachment - " + apartmentTitle,
                "<p>Dear " + tenantName + ", please find your formalized lease contract attached.</p>",
                os.toByteArray(),
                "Contract_" + contractNum + ".pdf"
        );
    }

    private void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new ApiException("Contract end date must be after start date");
        }
    }

    private void validateContractStartAgainstReservation(LocalDate startDate, LocalDate reservationDate) {
        if (startDate.isBefore(reservationDate)) {
            throw new ApiException("Contract start date cannot be before the reservation date");
        }
    }

}
