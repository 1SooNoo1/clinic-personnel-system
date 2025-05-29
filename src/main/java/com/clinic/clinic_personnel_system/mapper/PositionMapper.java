package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.PositionDTO;
import com.clinic.clinic_personnel_system.models.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    @Mapping(target = "id", source = "id")
    PositionDTO toDto(Position position);

    @Mapping(target = "id", ignore = true)
    Position toEntity(PositionDTO dto);
} 