package com.pesa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDto user;
    private String message;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserDto {
    private Long id;
    private String phoneNumber;
    private String fullName;
    private String email;
}
