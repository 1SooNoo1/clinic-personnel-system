package com.clinic.clinic_personnel_system.mapper;

import com.clinic.clinic_personnel_system.dto.PositionDTO;
import com.clinic.clinic_personnel_system.models.Position;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PositionMapper {
    Position toEntity(PositionDTO dto);
    PositionDTO toDto(Position position);
}