package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Application;
import com.clinic.clinic_personnel_system.models.User;
import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.repositories.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repository;

    @Override
    public Application apply(User user, Vacancy vacancy, String message) {
        repository.findByUserAndVacancy(user, vacancy).ifPresent(existing -> {
            throw new RuntimeException("Вы уже откликались на эту вакансию");
        });

        Application app = new Application();
        app.setUser(user);
        app.setVacancy(vacancy);
        app.setMessage(message);
        return repository.save(app);
    }

    @Override
    public List<Application> getByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Application> getByVacancy(Long vacancyId) {
        return repository.findByVacancyId(vacancyId);
    }
}
