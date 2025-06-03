package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.ApplicationDTO;
import com.clinic.clinic_personnel_system.models.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userPhone", source = "user.phone")
    @Mapping(target = "userFullName", source = "user.fullName")
    @Mapping(target = "vacancyId", source = "vacancy.id")
    @Mapping(target = "departmentName", source = "vacancy.department.name")
    @Mapping(target = "positionTitle", source = "vacancy.position.title")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "message", source = "message")
    ApplicationDTO toDto(Application application);
}
