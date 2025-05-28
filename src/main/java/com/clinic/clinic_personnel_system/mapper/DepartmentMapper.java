package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.DepartmentDTO;
import com.clinic.clinic_personnel_system.models.Department;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentDTO dto);
    DepartmentDTO toDto(Department department);
}