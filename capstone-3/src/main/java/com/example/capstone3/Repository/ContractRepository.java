package com.example.capstone3.Repository;

import com.example.capstone3.Enums.ContractStatus;
import com.example.capstone3.Models.Contract;
import com.example.capstone3.Models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    Contract findContractById(Integer id);

    //Contract findContractByReservation_User_IdAndReservation_Apartment_IdAndStatus(Integer userId, Integer apartmentId, ContractStatus status);

}
