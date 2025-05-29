package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmploymentHistoryDTO {
    private Long id;
    private Long departmentId;
    private String departmentName;
    private Long positionId;
    private String positionTitle;
    private LocalDate startDate;
    private LocalDate endDate;
}
