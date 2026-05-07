package com.pesa.dto;

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
    private final MaritalStatus maritalStatus;
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