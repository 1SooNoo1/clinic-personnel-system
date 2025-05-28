package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);

    Employee transferEmployee(Long id, Long departmentId, Long positionId);
    Employee dismissEmployee(Long id, LocalDate dismissalDate);
}
