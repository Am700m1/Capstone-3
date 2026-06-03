package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.MaintenanceRequestDTOIn;
import com.example.capstone3.Service.MaintenanceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenance-request")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping("/get")
    public ResponseEntity<?> getMaintenanceRequests() {
        return ResponseEntity.status(200).body(maintenanceRequestService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMaintenanceRequest(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(maintenanceRequestService.getMaintenanceRequest(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMaintenanceRequest(@RequestBody @Valid MaintenanceRequestDTOIn maintenanceRequestDTOIn) {
        maintenanceRequestService.addMaintenanceRequest(maintenanceRequestDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Maintenance request added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMaintenanceRequest(@PathVariable Integer id, @RequestBody @Valid MaintenanceRequestDTOIn maintenanceRequestDTOIn) {
        maintenanceRequestService.updateMaintenanceRequest(id, maintenanceRequestDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Maintenance request updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMaintenanceRequest(@PathVariable Integer id) {
        maintenanceRequestService.deleteMaintenanceRequest(id);
        return ResponseEntity.status(200).body(new ApiResponse("Maintenance request deleted successfully"));
    }
}
