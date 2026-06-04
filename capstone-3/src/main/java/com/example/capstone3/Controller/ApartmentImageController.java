package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ApartmentImageDTOIn;
import com.example.capstone3.Service.ApartmentImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/apartment-image")
@RequiredArgsConstructor
public class ApartmentImageController {

    private final ApartmentImageService apartmentImageService;

    @GetMapping("/get")
    public ResponseEntity<?> getApartmentImages() {
        return ResponseEntity.status(200).body(apartmentImageService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getApartmentImage(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(apartmentImageService.getApartmentImage(id));
    }

    @PostMapping("/add/{apartment_id}")
    public ResponseEntity<?> addApartmentImage(@RequestBody @Valid ApartmentImageDTOIn apartmentImageDTOIn, @PathVariable Integer apartment_id) {
        apartmentImageService.addApartmentImage(apartmentImageDTOIn, apartment_id);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment image added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateApartmentImage(@PathVariable Integer id, @RequestBody @Valid ApartmentImageDTOIn apartmentImageDTOIn) {
        apartmentImageService.updateApartmentImage(id, apartmentImageDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment image updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteApartmentImage(@PathVariable Integer id) {
        apartmentImageService.deleteApartmentImage(id);
        return ResponseEntity.status(200).body(new ApiResponse("Apartment image deleted successfully"));
    }
}
