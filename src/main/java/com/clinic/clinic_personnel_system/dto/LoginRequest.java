package com.clinic.clinic_personnel_system.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String password;
}