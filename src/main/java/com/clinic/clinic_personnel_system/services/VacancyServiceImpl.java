package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository repo;

    @Override
    public List<Vacancy> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Vacancy> getOpen() {
        return repo.findByIsOpenTrue();
    }

    @Override
    public Vacancy getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Вакансия не найдена"));
    }

    @Override
    public Vacancy create(Vacancy vacancy) {
        return repo.save(vacancy);
    }

    @Override
    public Vacancy update(Long id, Vacancy vacancy) {
        Vacancy existing = getById(id);
        existing.setDescription(vacancy.getDescription());
        existing.setIsOpen(vacancy.getIsOpen());
        existing.setDepartment(vacancy.getDepartment());
        existing.setPosition(vacancy.getPosition());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
