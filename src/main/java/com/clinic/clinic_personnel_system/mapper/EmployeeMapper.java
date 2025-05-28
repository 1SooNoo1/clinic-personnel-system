package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.EmployeeDTO;
import com.clinic.clinic_personnel_system.models.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EmployeeMapper {

    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "position.id", source = "positionId")
    Employee toEntity(EmployeeDTO dto);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "position.id", target = "positionId")
    EmployeeDTO toDto(Employee employee);

    // ➤ Обновление существующей сущности
    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "position.id", source = "positionId")
    void updateFromDto(EmployeeDTO dto, @MappingTarget Employee entity);
}