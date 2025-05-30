package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.dto.DepartmentStatsDTO;

import java.time.LocalDate;

public interface DepartmentStatsService {
    DepartmentStatsDTO getStatsForDepartment(Long departmentId, LocalDate from, LocalDate to);
}
