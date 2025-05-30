package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.VacancyDTO;
import com.clinic.clinic_personnel_system.models.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "positionId", source = "position.id")
    @Mapping(target = "positionTitle", source = "position.title")
    VacancyDTO toDto(Vacancy vacancy);
}
