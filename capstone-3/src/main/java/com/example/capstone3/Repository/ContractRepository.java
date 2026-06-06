package com.example.capstone3.Repository;

import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Models.Contract;
import com.example.capstone3.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    Contract findContractById(Integer id);

    List<Contract> findContractsByReservation_User_Id(Integer reservationUserId);

    List<Contract> findContractsByReservation_Apartment_Owner_Id(Integer reservationApartmentOwnerId);

    Contract findByReservation_User_IdAndReservation_Apartment_IdAndContractStatus(
            Integer userId, Integer apartmentId, ContractStatus contractStatus);

    List<Contract> findContractsByContractStatusAndEndDateBefore(ContractStatus status, LocalDate date);
}
