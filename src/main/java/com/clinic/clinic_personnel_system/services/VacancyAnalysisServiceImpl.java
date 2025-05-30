package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.models.Employee;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.models.Vacancy;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import com.clinic.clinic_personnel_system.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyAnalysisServiceImpl implements VacancyAnalysisService {

    private final EmploymentHistoryRepository historyRepository;
    private final VacancyRepository vacancyRepository;

    @Override
    public List<Employee> findMatchingEmployees(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new RuntimeException("Вакансия не найдена"));

        List<EmploymentHistory> all = historyRepository.findAll();

        return all.stream()
                .filter(h -> h.getPosition().getId().equals(vacancy.getPosition().getId()) &&
                             h.getDepartment().getId().equals(vacancy.getDepartment().getId()))
                .map(EmploymentHistory::getEmployee)
                .distinct()
                .collect(Collectors.toList());
    }
}
