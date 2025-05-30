package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationDTO {
    private Long id;
    private Long userId;
    private String userPhone;
    private String userFullName;
    private Long vacancyId;
    private String departmentName;
    private String positionTitle;
    private String message;
    private LocalDateTime createdAt;
}
