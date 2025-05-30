package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
                .orElseThrow(() -> {
                    log.warn("Попытка доступа к несуществующей вакансии ID={}", id);
                    return new RuntimeException("Вакансия не найдена");
                });
    }

    @Override
    public Vacancy create(Vacancy vacancy) {
        Vacancy saved = repo.save(vacancy);
        log.info("Создана новая вакансия: должность={}, отделение={}, ID={}",
                saved.getPosition().getTitle(), saved.getDepartment().getName(), saved.getId());
        return saved;
    }

    @Override
    public Vacancy update(Long id, Vacancy vacancy) {
        Vacancy existing = getById(id);
        existing.setDescription(vacancy.getDescription());
        existing.setIsOpen(vacancy.getIsOpen());
        existing.setDepartment(vacancy.getDepartment());
        existing.setPosition(vacancy.getPosition());

        Vacancy updated = repo.save(existing);
        log.info("Обновлена вакансия ID={}, открыта={}", id, updated.getIsOpen());
        return updated;
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
        log.warn("Удалена вакансия ID={}", id);
    }
}
