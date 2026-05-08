package com.pesa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentInfoRequest {
    private String employmentStatus;
    private String employerName;
    private String employerAddress;
    private String tinNumber;
    private String businessName;
    private String businessTinNumber;
    private String businessRegistrationNumber;
    private int numberOfDependants;
}
