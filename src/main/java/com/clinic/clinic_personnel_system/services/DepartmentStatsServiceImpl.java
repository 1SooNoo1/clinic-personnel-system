package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.dto.DepartmentStatsDTO;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.models.EmploymentHistory;
import com.clinic.clinic_personnel_system.repositories.EmploymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentStatsServiceImpl implements DepartmentStatsService {

    private final EmploymentHistoryRepository historyRepository;
    private final DepartmentService departmentService;

    @Override
    public DepartmentStatsDTO getStatsForDepartment(Long departmentId, LocalDate from, LocalDate to) {
        Department department = departmentService.findById(departmentId);

        List<EmploymentHistory> all = historyRepository.findAll();

        DepartmentStatsDTO dto = new DepartmentStatsDTO();
        dto.setDepartmentId(departmentId);
        dto.setDepartmentName(department.getName());

        dto.setCountAtStart((int) all.stream()
                .filter(h -> h.getDepartment().getId().equals(departmentId))
                .filter(h -> h.getStartDate().isBefore(from) &&
                            (h.getEndDate() == null || h.getEndDate().isAfter(from)))
                .count());

        dto.setHiredDuringPeriod((int) all.stream()
                .filter(h -> h.getDepartment().getId().equals(departmentId))
                .filter(h -> !h.getStartDate().isBefore(from) && !h.getStartDate().isAfter(to))
                .count());

        dto.setDismissedDuringPeriod((int) all.stream()
                .filter(h -> h.getDepartment().getId().equals(departmentId))
                .filter(h -> h.getEndDate() != null)
                .filter(h -> !h.getEndDate().isBefore(from) && !h.getEndDate().isAfter(to))
                .count());

        dto.setCountAtEnd((int) all.stream()
                .filter(h -> h.getDepartment().getId().equals(departmentId))
                .filter(h -> h.getStartDate().isBefore(to.plusDays(1)) &&
                            (h.getEndDate() == null || h.getEndDate().isAfter(to)))
                .count());

        return dto;
    }
}
