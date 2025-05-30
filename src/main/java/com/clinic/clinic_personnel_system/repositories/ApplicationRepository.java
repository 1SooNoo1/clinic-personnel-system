package com.clinic.clinic_personnel_system.repositories;

import com.clinic.clinic_personnel_system.models.Application;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.models.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUserAndVacancy(User user, Vacancy vacancy);
    List<Application> findByVacancyId(Long vacancyId);
    List<Application> findByUserId(Long userId);
}
