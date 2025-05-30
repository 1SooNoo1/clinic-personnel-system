package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

@Data
public class VacancyDTO {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String positionTitle;
    private String description;
    private Boolean isOpen;
}
