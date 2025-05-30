package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

@Data
public class DepartmentStatsDTO {
    private Long departmentId;
    private String departmentName;
    private int countAtStart;
    private int hiredDuringPeriod;
    private int dismissedDuringPeriod;
    private int countAtEnd;
}
