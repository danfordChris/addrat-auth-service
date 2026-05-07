package com.pesa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+255\\d{9}$", message = "Phone number must be in format +255XXXXXXXXX")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
