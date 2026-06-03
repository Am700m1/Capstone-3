package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ReservationDTOIn;
import com.example.capstone3.Service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/get")
    public ResponseEntity<?> getReservations() {
        return ResponseEntity.status(200).body(reservationService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReservation(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reservationService.getReservation(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReservation(@RequestBody @Valid ReservationDTOIn reservationDTOIn) {
        reservationService.addReservation(reservationDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Reservation added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Integer id, @RequestBody @Valid ReservationDTOIn reservationDTOIn) {
        reservationService.updateReservation(id, reservationDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Reservation updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Integer id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Reservation deleted successfully"));
    }
}
