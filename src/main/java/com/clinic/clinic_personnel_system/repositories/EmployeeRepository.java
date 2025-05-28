package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // можно будет добавлять кастомные методы
}
