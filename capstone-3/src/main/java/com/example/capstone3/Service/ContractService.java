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

    public void addContract(ContractDTOIn contractDTOIn, Integer reservation_id) {
        Reservation reservation = reservationRepository.findReservationById(reservation_id);
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new ApiException("Cannot generate a contract! The reservation must be APPROVED by the owner first.");
        }

        Contract contract = new Contract();
        contract.setReservation(reservation);
        contract.setContractNumber(contractDTOIn.getContractNumber());
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setMonthlyRent(contractDTOIn.getMonthlyRent());
        contract.setSecurityDeposit(contractDTOIn.getSecurityDeposit());
        contract.setSigned(contractDTOIn.getSigned() == null ? false : contractDTOIn.getSigned());
        contract.setSignedDate(contractDTOIn.getSignedDate());
        contract.setPdfPath(contractDTOIn.getPdfPath());
        contract.setContractStatus(ContractStatus.PENDING);
        contractRepository.save(contract);
    }

    public void updateContract(Integer id, ContractDTOIn contractDTOIn) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
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

        Reservation reservation = contract.getReservation();

        contract.setSigned(true);
        contract.setContractStatus(ContractStatus.ACTIVE);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
        contractRepository.save(contract);


        Apartment apartment = apartmentRepository.findApartmentById(contract.getReservation().getApartment().getId());
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

        whatsAppService.notifyTenantContractEnded(reservation.getUser(), apartment);
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

        if (!language.equalsIgnoreCase("English") && !language.equalsIgnoreCase("Arabic")) {
            throw new ApiException("Language must be either 'English' or 'Arabic'");
        }

        try {
            String aiJsonString = aiService.analyzeContract(contract, language);
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
        String waterIncluded  = Boolean.TRUE.equals(apt.getWaterIncluded()) ? "Yes" : "No";
        String internetIncluded   = Boolean.TRUE.equals(apt.getInternetIncluded()) ? "Yes" : "No";
        String electricityIncluded = Boolean.TRUE.equals(apt.getElectricityIncluded()) ? "Yes" : "No";
        String allowedTenantType  = apt.getAllowedTenantType() != null ? apt.getAllowedTenantType() : "N/A";
        String floorNumber        = apt.getFloorNumber() != null ? String.valueOf(apt.getFloorNumber()) : "N/A";

        Building building     = contract.getReservation().getApartment().getBuilding();
        String hasParking     = Boolean.TRUE.equals(building.getHasParking()) ? "Yes" : "No";
        String hasElevator    = Boolean.TRUE.equals(building.getHasElevator()) ? "Yes" : "No";
        String hasSecurity    = Boolean.TRUE.equals(building.getHasSecurity()) ? "Yes" : "No";
        String petsAllowed    = Boolean.TRUE.equals(building.getPetsAllowed()) ? "Yes" : "No";

        String pdfHtml = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; padding: 40px; color: #2d3748; }" +
                "h1 { color: #1a365d; font-size: 24px; margin-bottom: 5px; text-align: center; }" +
                ".subtitle { color: #718096; font-size: 13px; text-align: center; }" +
                ".section-title { background-color: #1a365d; color: white; padding: 8px 12px; margin-top: 25px; font-size: 14px; }" +
                "table { width: 100%; border-collapse: collapse; }" +
                "td { padding: 10px 12px; border: 1px solid #e2e8f0; font-size: 13px; }" +
                ".label { font-weight: bold; background-color: #f7fafc; width: 40%; }" +
                ".footer { margin-top: 40px; font-size: 12px; color: #718096; text-align: center; border-top: 1px solid #e2e8f0; padding-top: 15px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>OFFICIAL LEASE AGREEMENT</h1>" +
                "<div class='subtitle'>Contract No: " + contractNum + " | Status: " + status + "</div>" +

                "<div class='section-title'>PROPERTY INFORMATION</div>" +
                "<table>" +
                "<tr><td class='label'>Building Name</td><td>" + buildingName + "</td></tr>" +
                "<tr><td class='label'>Apartment</td><td>" + apartmentTitle + "</td></tr>" +
                "<tr><td class='label'>District</td><td>" + district + "</td></tr>" +
                "<tr><td class='label'>Floor Number</td><td>" + floorNumber + "</td></tr>" +
                "<tr><td class='label'>Allowed Tenant Type</td><td>" + allowedTenantType + "</td></tr>" +
                "</table>" +

                "<div class='section-title'>BUILDING FACILITIES</div>" +
                "<table>" +
                "<tr><td class='label'>Parking</td><td>" + hasParking + "</td></tr>" +
                "<tr><td class='label'>Elevator</td><td>" + hasElevator + "</td></tr>" +
                "<tr><td class='label'>Security</td><td>" + hasSecurity + "</td></tr>" +
                "<tr><td class='label'>Pets Allowed</td><td>" + petsAllowed + "</td></tr>" +
                "</table>" +

                "<div class='section-title'>APARTMENT INCLUSIONS</div>" +
                "<table>" +
                "<tr><td class='label'>Furnished</td><td>" + furnished + "</td></tr>" +
                "<tr><td class='label'>Water Included</td><td>" + waterIncluded + "</td></tr>" +
                "<tr><td class='label'>Internet Included</td><td>" + internetIncluded + "</td></tr>" +
                "<tr><td class='label'>Electricity Included</td><td>" + electricityIncluded + "</td></tr>" +
                "</table>" +

                "<div class='section-title'>OWNER INFORMATION</div>" +
                "<table>" +
                "<tr><td class='label'>Owner Name</td><td>" + ownerName + "</td></tr>" +
                "<tr><td class='label'>Email</td><td>" + ownerEmail + "</td></tr>" +
                "<tr><td class='label'>Phone</td><td>" + ownerPhone + "</td></tr>" +
                "</table>" +

                "<div class='section-title'>TENANT INFORMATION</div>" +
                "<table>" +
                "<tr><td class='label'>Tenant Name</td><td>" + tenantName + "</td></tr>" +
                "<tr><td class='label'>Email</td><td>" + tenantEmail + "</td></tr>" +
                "<tr><td class='label'>Phone</td><td>" + tenantPhone + "</td></tr>" +
                "</table>" +

                "<div class='section-title'>CONTRACT TERMS</div>" +
                "<table>" +
                "<tr><td class='label'>Start Date</td><td>" + startDate + "</td></tr>" +
                "<tr><td class='label'>End Date</td><td>" + endDate + "</td></tr>" +
                "<tr><td class='label'>Monthly Rent</td><td>" + monthlyRent + " SAR</td></tr>" +
                "<tr><td class='label'>Security Deposit</td><td>" + secDeposit + " SAR</td></tr>" +
                "<tr><td class='label'>Signed</td><td>" + signed + "</td></tr>" +
                "<tr><td class='label'>Signed Date</td><td>" + signedDate + "</td></tr>" +
                "<tr><td class='label'>Tenant Notes</td><td>" + reservationMsg + "</td></tr>" +
                "</table>" +

                "<div class='footer'>This document is an official lease agreement generated by the Smart Rental Platform. " +
                "Both parties are bound by the terms stated above.</div>" +
                "</body>" +
                "</html>";

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

}
