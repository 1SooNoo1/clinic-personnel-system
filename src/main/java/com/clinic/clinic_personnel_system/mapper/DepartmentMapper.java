package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.DepartmentDTO;
import com.clinic.clinic_personnel_system.models.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", source = "id")
    DepartmentDTO toDto(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Department toEntity(DepartmentDTO dto);
} 