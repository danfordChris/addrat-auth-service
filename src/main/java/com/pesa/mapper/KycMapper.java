package com.pesa.mapper;

import org.springframework.stereotype.Component;

import com.pesa.dto.UserProfileResponse;
import com.pesa.entity.CreditBoardScore;
import com.pesa.entity.KycProfile;
import com.pesa.entity.User;

@Component
public class KycMapper {

    public KycProfile.Gender mapGender(KycProfile.Gender gender) {
        return gender;
    }

    public KycProfile.MaritalStatus mapMaritalStatus(KycProfile.MaritalStatus status) {
        return status;
    }

    public String nullSafe(String value) {
        return value == null ? "" : value;
    }

    public UserProfileResponse toUserProfileResponse(User user, KycProfile kyc, CreditBoardScore credit) {
        return UserProfileResponse.builder()
                .id(String.valueOf(user.getId()))
                .phoneNumber(user.getPhoneNumber())
                .fullName(kyc != null ? nullSafe(kyc.getFullName()) : "")
                .kycStatus(kyc != null && kyc.getStatus() != null ? kyc.getStatus().name() : "NOT_STARTED")
                .rejectionReason(kyc != null ? kyc.getRejectionReason() : null)
                .creditLimit(credit != null && credit.getLoanLimit() != null ? credit.getLoanLimit().toPlainString() : "0")
                .eligible(credit != null && Boolean.TRUE.equals(credit.getEligible()))
                .completionStep(kyc != null && kyc.getCompletionStep() != null ? kyc.getCompletionStep().ordinal() : 0)
                .dateOfBirth(kyc != null ? kyc.getDateOfBirth() : null)
                .gender(kyc != null ? mapGender(kyc.getGender()) : null)
                .idType(kyc != null && kyc.getIdType() != null ? kyc.getIdType().name() : null)
                .idNumber(kyc != null ? kyc.getIdNumber() : null)
                .residenceAddress(kyc != null ? kyc.getResidenceAddress() : null)
                .businessDetails(kyc != null ? kyc.getBusinessDetails() : null)
                .maritalStatus(kyc != null ? mapMaritalStatus(kyc.getMaritalStatus()) : null)
                .numberOfDependents(kyc != null ? kyc.getNumberOfDependents() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(kyc != null ? kyc.getUpdatedAt() : null)
                .approvedAt(kyc != null ? kyc.getApprovedAt() : null)
                .build();
    }
}
