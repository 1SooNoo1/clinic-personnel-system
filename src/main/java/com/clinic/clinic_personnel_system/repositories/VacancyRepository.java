package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findByIsOpenTrue();
    List<Vacancy> findByDepartmentId(Long departmentId);
    List<Vacancy> findByPositionId(Long positionId);
}
