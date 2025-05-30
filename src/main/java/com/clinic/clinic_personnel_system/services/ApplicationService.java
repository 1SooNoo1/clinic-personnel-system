package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Application;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.models.Vacancy;

import java.util.List;

public interface ApplicationService {
    Application apply(User user, Vacancy vacancy, String message);
    List<Application> getByUser(Long userId);
    List<Application> getByVacancy(Long vacancyId);
}
