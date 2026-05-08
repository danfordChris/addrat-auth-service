package com.pesa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pesa.entity.KycProfile.Gender;
import com.pesa.entity.KycProfile.MaritalStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private final String id;
    private final String phoneNumber;
    private final String fullName;

    // KYC related (aligned with entity)
    private final String kycStatus;
    private final String rejectionReason;

    // Optional but useful profile info from KYC
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final String idType;
    private final String idNumber;
    private final String residenceAddress;
    private final String businessDetails;
    private final String employmentStatus;
    private final String employerName;
    private final String employerAddress;
    private final String tinNumber;
    private final String businessName;
    private final String businessTinNumber;
    private final String businessRegistrationNumber;
    private final String incomeRange;
    private final String incomeSource;
    private final BigDecimal loanAmountRequested;
    private final String loanPurpose;
    private final Integer repaymentPeriodMonths;
    private final MaritalStatus maritalStatus;

    @JsonProperty("numberOfDependants")
    private final Integer numberOfDependents;

    // App-specific fields
    private final String creditLimit;
    private final boolean eligible;

    private final Integer completionStep;

    // timestamps
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime approvedAt;

}