package com.clinic.clinic_personnel_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для отделения")
public class DepartmentDTO {
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; 

    @NotBlank(message = "Название отделения обязательно")
    @Schema(example = "Хирургия", description = "Название отделения")
    private String name;
}