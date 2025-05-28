package com.clinic.clinic_personnel_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для должности")
public class PositionDTO {

    @NotBlank(message = "Название должности не может быть пустым")
    @Schema(example = "Врач", description = "Название должности")
    private String title;
}