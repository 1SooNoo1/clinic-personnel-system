package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Employee;

import java.util.List;

public interface VacancyAnalysisService {
    List<Employee> findMatchingEmployees(Long vacancyId);
}
