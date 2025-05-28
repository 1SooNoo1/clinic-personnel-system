package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phone;
    private String password;
    private String fullName;
    private String roles;
}