package com.example.capstone3.Repository;

import com.example.capstone3.Models.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRequest, Integer> {

    MaintenanceRequest findMaintenanceRequestById(Integer id);

}
