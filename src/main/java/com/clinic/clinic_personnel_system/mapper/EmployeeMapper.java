package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.models.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "positionId", source = "position.id")
    EmployeeDTO toDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "position", ignore = true)
    Employee toEntity(EmployeeDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "position", ignore = true)
    void updateFromDto(EmployeeDTO dto, @MappingTarget Employee employee);

    @AfterMapping
    default void linkDepartmentsAndPositions(@MappingTarget Employee employee, EmployeeDTO dto) {
        // Для ручной подстановки department и position в сервисе
    }
}
