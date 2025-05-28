package com.clinic.clinic_personnel_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO для сотрудника")
public class EmployeeDTO {

    @NotBlank(message = "Имя сотрудника обязательно")
    @Schema(example = "Иван Иванов", description = "Полное имя сотрудника")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    @Schema(example = "ivan@example.com", description = "Email сотрудника")
    private String email;

    @Schema(example = "+79991234567", description = "Телефон")
    private String phone;

    @Past(message = "Дата рождения должна быть в прошлом")
    @Schema(example = "1980-01-01", description = "Дата рождения")
    private LocalDate birthDate;

    @PastOrPresent(message = "Дата трудоустройства не может быть в будущем")
    @Schema(example = "2020-01-01", description = "Дата трудоустройства")
    private LocalDate employmentDate;

    @Schema(example = "null", description = "Дата увольнения")
    private LocalDate dismissalDate;

    @Schema(example = "true", description = "Статус активности")
    private Boolean active = true;

    @Schema(description = "ID отделения")
    private Long departmentId;

    @Schema(description = "ID должности")
    private Long positionId;
}