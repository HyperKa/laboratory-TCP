package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {
    // Getters and setters
    private String currentPassword;
    private String newPassword;

}