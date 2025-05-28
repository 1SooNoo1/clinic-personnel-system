package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
