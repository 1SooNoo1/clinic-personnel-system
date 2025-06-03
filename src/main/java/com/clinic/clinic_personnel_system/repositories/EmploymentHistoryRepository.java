package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    List<EmploymentHistory> findByEmployeeIdOrderByStartDate(Long employeeId);
    EmploymentHistory findFirstByEmployeeIdAndEndDateIsNullOrderByStartDateDesc(Long employeeId);
}
