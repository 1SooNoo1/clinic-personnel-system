package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Vacancy;

import java.util.List;

public interface VacancyService {
    List<Vacancy> getAll();
    List<Vacancy> getOpen();
    Vacancy getById(Long id);
    Vacancy create(Vacancy vacancy);
    Vacancy update(Long id, Vacancy vacancy);
    void delete(Long id);
}
