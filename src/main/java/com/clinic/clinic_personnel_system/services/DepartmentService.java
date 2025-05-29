package com.clinic.clinic_personnel_system.services;

import com.clinic.clinic_personnel_system.dto.DepartmentDTO;
import com.clinic.clinic_personnel_system.models.Department;
import com.clinic.clinic_personnel_system.repositories.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    public Department save(Department d) {
        return departmentRepository.save(d);
    }

    public void delete(Long id) {
        departmentRepository.deleteById(id);
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отделение не найдено с ID: " + id));
    }

    public Department update(Long id, DepartmentDTO dto) {
        Department existing = findById(id);
        existing.setName(dto.getName());
        return departmentRepository.save(existing);
    }
}
