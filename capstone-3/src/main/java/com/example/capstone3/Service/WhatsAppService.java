package com.example.capstone3.Service;

import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.MaintenanceRequest;
import com.example.capstone3.Models.Owner;
import com.example.capstone3.Models.Reservation;
import com.example.capstone3.Models.Review;
import com.example.capstone3.Models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final RestTemplate restTemplate;

    @Value("${ultramsg.token}")
    private String token;

    @Value("${ultramsg.instance.id}")
    private String instanceId;

    // ===================== CORE SEND METHOD =====================

    private void sendMessage(String phoneNumber, String message) {
        try {
            String url = "https://api.ultramsg.com/" + instanceId + "/messages/chat";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("token", token);
            params.add("to", phoneNumber);
            params.add("body", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            restTemplate.postForEntity(url, request, String.class);

        } catch (Exception e) {
            System.err.println("WhatsApp notification failed for " + phoneNumber + ": " + e.getMessage());
        }
    }

    // ===================== TENANT NOTIFICATIONS =====================

    public void notifyTenantReservationAccepted(User tenant, Apartment apartment) {
        String message = "Hello " + tenant.getFullName() + ",\n\n" +
                "Your reservation for *" + apartment.getTitle() + "* has been *ACCEPTED*.\n" +
                "District: " + apartment.getBuilding().getDistrict() + "\n" +
                "Monthly Rent: " + apartment.getMonthlyRent() + " SAR\n\n" +
                "Please wait for your contract to be generated.\n" +
                "Smart Rental Platform";
        sendMessage(tenant.getPhoneNumber(), message);
    }

    public void notifyTenantReservationRejected(User tenant, Apartment apartment) {
        String message = "Hello " + tenant.getFullName() + ",\n\n" +
                "Unfortunately, your reservation for *" + apartment.getTitle() + "* has been *REJECTED*.\n\n" +
                "You can browse other available apartments on the platform.\n" +
                "Smart Rental Platform";
        sendMessage(tenant.getPhoneNumber(), message);
    }

    public void notifyTenantContractEnded(User tenant, Apartment apartment) {
        String message = "Hello " + tenant.getFullName() + ",\n\n" +
                "Your rental contract for *" + apartment.getTitle() + "* has officially *ENDED*.\n\n" +
                "Please make sure to hand over the apartment keys and submit your final review.\n" +
                "Smart Rental Platform";
        sendMessage(tenant.getPhoneNumber(), message);
    }

    public void notifyTenantMaintenanceUpdated(User tenant, MaintenanceRequest maintenanceRequest) {
        String message = "Hello " + tenant.getFullName() + ",\n\n" +
                "Your maintenance request has been updated.\n" +
                "Issue: " + maintenanceRequest.getDescription() + "\n" +
                "Status: *" + maintenanceRequest.getStatus() + "*\n\n" +
                "Smart Rental Platform";
        sendMessage(tenant.getPhoneNumber(), message);
    }

    // ===================== OWNER NOTIFICATIONS =====================

    public void notifyOwnerContractAccepted(Owner owner, Apartment apartment, User tenant) {
        String message = "Hello " + owner.getFullName() + ",\n\n" +
                "The contract for apartment *" + apartment.getTitle() + "* has been *ACCEPTED* by the tenant.\n" +
                "Tenant: " + tenant.getFullName() + "\n" +
                "Phone: " + tenant.getPhoneNumber() + "\n\n" +
                "The apartment status has been updated to RENTED.\n" +
                "Smart Rental Platform";
        sendMessage(owner.getPhoneNumber(), message);
    }

    public void notifyOwnerContractRejected(Owner owner, Apartment apartment, User tenant) {
        String message = "Hello " + owner.getFullName() + ",\n\n" +
                "The contract for apartment *" + apartment.getTitle() + "* has been *REJECTED* by the tenant.\n" +
                "Tenant: " + tenant.getFullName() + "\n" +
                "Phone: " + tenant.getPhoneNumber() + "\n\n" +
                "The apartment status has been updated back to AVAILABLE.\n" +
                "Smart Rental Platform";
        sendMessage(owner.getPhoneNumber(), message);
    }

    public void notifyOwnerNewReservation(Owner owner, Reservation reservation) {
        String message = "Hello " + owner.getFullName() + ",\n\n" +
                "You have a *NEW RESERVATION* request.\n" +
                "Apartment: " + reservation.getApartment().getTitle() + "\n" +
                "Tenant: " + reservation.getUser().getFullName() + "\n" +
                "Date: " + reservation.getReservationDate() + "\n\n" +
                "Please log in to accept or reject the reservation.\n" +
                "Smart Rental Platform";
        sendMessage(owner.getPhoneNumber(), message);
    }

    public void notifyOwnerContractEnded(Owner owner, Apartment apartment) {
        String message = "Hello " + owner.getFullName() + ",\n\n" +
                "The contract for apartment *" + apartment.getTitle() + "* has *ENDED*.\n" +
                "The apartment has been placed *UNDER MAINTENANCE* for your inspection.\n\n" +
                "Please log in to review and update the apartment status.\n" +
                "Smart Rental Platform";
        sendMessage(owner.getPhoneNumber(), message);
    }

    public void notifyOwnerNewReview(Owner owner, Apartment apartment, Review review) {
        String message = "Hello " + owner.getFullName() + ",\n\n" +
                "A new review has been posted on *" + apartment.getTitle() + "*.\n" +
                "Rating: " + review.getRating() + "/5\n" +
                "Comment: " + review.getComment() + "\n\n" +
                "Smart Rental Platform";
        sendMessage(owner.getPhoneNumber(), message);
    }
}
