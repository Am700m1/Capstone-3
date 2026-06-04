package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ContractDTOIn;
import com.example.capstone3.Service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/get")
    public ResponseEntity<?> getContracts() {
        return ResponseEntity.status(200).body(contractService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getContract(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(contractService.getContract(id));
    }

    @PostMapping("/add/{reservation_id}")
    public ResponseEntity<?> addContract(@RequestBody @Valid ContractDTOIn contractDTOIn, @PathVariable Integer reservation_id) {
        contractService.addContract(contractDTOIn, reservation_id);
        return ResponseEntity.status(200).body(new ApiResponse("Contract added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateContract(@PathVariable Integer id, @RequestBody @Valid ContractDTOIn contractDTOIn) {
        contractService.updateContract(id, contractDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Contract updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Integer id) {
        contractService.deleteContract(id);
        return ResponseEntity.status(200).body(new ApiResponse("Contract deleted successfully"));
    }
}
