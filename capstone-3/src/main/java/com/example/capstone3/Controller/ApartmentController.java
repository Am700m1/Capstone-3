package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ApartmentDTOIn;
import com.example.capstone3.Service.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    //^^^^^^^CRUD^^^^^^^^


    @GetMapping("/get/underpriced")
    public ResponseEntity<?> getUnderpricedApartments() {
        return ResponseEntity.status(200).body(apartmentService.getUnderpricedApartments());
    }
}
