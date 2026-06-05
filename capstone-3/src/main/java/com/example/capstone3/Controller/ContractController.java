package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.ContractDTOIn;
import com.example.capstone3.DTO.Out.ContractDTOOut;
import com.example.capstone3.Service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{contractId}/accept/{userId}")
    public ResponseEntity<?> acceptContract(@PathVariable Integer userId, @PathVariable Integer contractId) {
        contractService.acceptContract(userId, contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract accepted and activated successfully."));
    }

    @PutMapping("/{contractId}/reject/{userId}")
    public ResponseEntity<?> rejectContract(@PathVariable Integer userId, @PathVariable Integer contractId) {
        contractService.rejectContract(userId, contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract rejected and cancelled successfully."));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getContractsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(contractService.getContractsByUserId(userId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getContractsByOwnerId(@PathVariable Integer ownerId) {
        return ResponseEntity.status(200).body(contractService.getContractsByOwnerId(ownerId));
    }

    @PutMapping("/{contractId}/end/{ownerId}")
    public ResponseEntity<?> endContract(@PathVariable Integer ownerId, @PathVariable Integer contractId) {
        contractService.endContract(ownerId, contractId);
        return ResponseEntity.status(200).body(new ApiResponse("Contract officially ended successfully."));
    }

    @PutMapping("/{contractId}/renew/{userId}")
    public ResponseEntity<?> renewContract(@PathVariable Integer userId, @PathVariable Integer contractId, @RequestParam Integer extraMonths) {
        contractService.renewContract(userId, contractId, extraMonths);
        return ResponseEntity.status(200).body(new ApiResponse("Contract renewed for " + extraMonths + " extra months."));
    }

    @GetMapping("/{contractId}/analyze/{userId}")
    public ResponseEntity<?> getContractAnalysis(@PathVariable Integer userId, @PathVariable Integer contractId, @RequestParam String language) {
        return ResponseEntity.status(200).body(contractService.getContractAnalysis(userId, contractId, language));
    }
}
