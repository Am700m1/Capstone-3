package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ApartmentDTOIn;
import com.example.capstone3.DTO.Out.ApartmentDTOOut;
import com.example.capstone3.Service.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/apartment")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService apartmentService;

    @GetMapping("/get")
    public ResponseEntity<?> getApartments() {
        return ResponseEntity.status(200).body(apartmentService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getApartment(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(apartmentService.getApartment(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addApartment(@RequestBody @Valid ApartmentDTOIn apartmentDTOIn) {
        apartmentService.addApartment(apartmentDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateApartment(@PathVariable Integer id, @RequestBody @Valid ApartmentDTOIn apartmentDTOIn) {
        apartmentService.updateApartment(id, apartmentDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteApartment(@PathVariable Integer id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment deleted successfully"));
    }

    @GetMapping("/get/underpriced")
    public ResponseEntity<?> getUnderpricedApartments() {
        return ResponseEntity.status(200).body(apartmentService.getUnderpricedApartments());
    }

    @GetMapping("/dashboard/{ownerId}")
    public ResponseEntity<java.util.Map<com.example.capstone3.Enums.ApartmentStatus, java.util.List<com.example.capstone3.DTO.Out.ApartmentDTOOut>>> getOwnerDashboard(@PathVariable Integer ownerId) {
        return ResponseEntity.status(200).body(apartmentService.getOwnerDashboard(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ApartmentDTOOut>> searchApartments(
            @RequestParam(required = false) Double minRent, @RequestParam(required = false) Double maxRent, @RequestParam(required = false) Integer bedrooms, @RequestParam(required = false) String district, @RequestParam(required = false) Boolean isFurnished) {

        return ResponseEntity.status(200).body(apartmentService.searchApartments(minRent, maxRent, bedrooms, district, isFurnished));
    }
}
