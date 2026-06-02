package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.ContractDTOIn;
import com.example.capstone3.DTO.Out.ContractDTOOut;
import com.example.capstone3.Models.Contract;
import com.example.capstone3.Models.Reservation;
import com.example.capstone3.Repository.ContractRepository;
import com.example.capstone3.Repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ReservationRepository reservationRepository;

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

    public void addContract(ContractDTOIn contractDTOIn) {
        Reservation reservation = reservationRepository.findReservationById(contractDTOIn.getReservationId());
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        Contract contract = new Contract();
        contract.setReservation(reservation);
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setRentAmount(contractDTOIn.getRentAmount());
        contract.setContractFilePath(contractDTOIn.getContractFilePath());
        contract.setStatus("ACTIVE");
        contractRepository.save(contract);
    }

    public void updateContract(Integer id, ContractDTOIn contractDTOIn) {
        Contract contract = contractRepository.findContractById(id);
        if (contract == null) {
            throw new ApiException("Contract not found");
        }
        Reservation reservation = reservationRepository.findReservationById(contractDTOIn.getReservationId());
        if (reservation == null) {
            throw new ApiException("Reservation not found");
        }
        contract.setReservation(reservation);
        contract.setStartDate(contractDTOIn.getStartDate());
        contract.setEndDate(contractDTOIn.getEndDate());
        contract.setRentAmount(contractDTOIn.getRentAmount());
        contract.setContractFilePath(contractDTOIn.getContractFilePath());
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
        contractDTOOut.setStartDate(contract.getStartDate());
        contractDTOOut.setEndDate(contract.getEndDate());
        contractDTOOut.setRentAmount(contract.getRentAmount());
        contractDTOOut.setContractFilePath(contract.getContractFilePath());
        contractDTOOut.setStatus(contract.getStatus());
        return contractDTOOut;
    }
}
