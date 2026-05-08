package com.pesa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pesa.entity.KycProfile;
import com.pesa.entity.KycProfile.KycStep;

public class KycResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String idType;
    private String idNumber;
    private String residenceAddress;
    private String businessDetails;
    private String employmentStatus;
    private String employerName;
    private String employerAddress;
    private String tinNumber;
    private String businessName;
    private String businessTinNumber;
    private String businessRegistrationNumber;
    private String incomeRange;
    private String incomeSource;
    private BigDecimal loanAmountRequested;
    private String loanPurpose;
    private Integer repaymentPeriodMonths;
    private String maritalStatus;

    @JsonProperty("numberOfDependants")
    private Integer numberOfDependents;
    private String status;
    private String rejectionReason;
    private KycStep completionStep;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;

    public KycResponse() {
    }

    public KycResponse(Long id, Long userId, String fullName, LocalDate dateOfBirth, String gender, String idType,
            String idNumber, String residenceAddress, String businessDetails, String maritalStatus,
            Integer numberOfDependents, String status, String rejectionReason, KycStep completionStep,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.idType = idType;
        this.idNumber = idNumber;
        this.residenceAddress = residenceAddress;
        this.businessDetails = businessDetails;
        this.maritalStatus = maritalStatus;
        this.numberOfDependents = numberOfDependents;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.completionStep = completionStep;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
    }

    public KycResponse(Long id, Long userId, String fullName, LocalDate dateOfBirth, String gender, String idType,
            String idNumber, String residenceAddress, String businessDetails, String employmentStatus,
            String employerName, String employerAddress, String tinNumber, String businessName,
            String businessTinNumber, String businessRegistrationNumber, String incomeRange, String incomeSource,
            BigDecimal loanAmountRequested, String loanPurpose, Integer repaymentPeriodMonths, String maritalStatus,
            Integer numberOfDependents, String status, String rejectionReason, KycStep completionStep,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime approvedAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.idType = idType;
        this.idNumber = idNumber;
        this.residenceAddress = residenceAddress;
        this.businessDetails = businessDetails;
        this.employmentStatus = employmentStatus;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
        this.tinNumber = tinNumber;
        this.businessName = businessName;
        this.businessTinNumber = businessTinNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.incomeRange = incomeRange;
        this.incomeSource = incomeSource;
        this.loanAmountRequested = loanAmountRequested;
        this.loanPurpose = loanPurpose;
        this.repaymentPeriodMonths = repaymentPeriodMonths;
        this.maritalStatus = maritalStatus;
        this.numberOfDependents = numberOfDependents;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.completionStep = completionStep;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.approvedAt = approvedAt;
    }

    public static KycResponseBuilder builder() {
        return new KycResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getResidenceAddress() {
        return residenceAddress;
    }

    public void setResidenceAddress(String residenceAddress) {
        this.residenceAddress = residenceAddress;
    }

    public String getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(String businessDetails) {
        this.businessDetails = businessDetails;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(String employerAddress) {
        this.employerAddress = employerAddress;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessTinNumber() {
        return businessTinNumber;
    }

    public void setBusinessTinNumber(String businessTinNumber) {
        this.businessTinNumber = businessTinNumber;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getIncomeRange() {
        return incomeRange;
    }

    public void setIncomeRange(String incomeRange) {
        this.incomeRange = incomeRange;
    }

    public String getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public BigDecimal getLoanAmountRequested() {
        return loanAmountRequested;
    }

    public void setLoanAmountRequested(BigDecimal loanAmountRequested) {
        this.loanAmountRequested = loanAmountRequested;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public Integer getRepaymentPeriodMonths() {
        return repaymentPeriodMonths;
    }

    public void setRepaymentPeriodMonths(Integer repaymentPeriodMonths) {
        this.repaymentPeriodMonths = repaymentPeriodMonths;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getNumberOfDependents() {
        return numberOfDependents;
    }

    public void setNumberOfDependents(Integer numberOfDependents) {
        this.numberOfDependents = numberOfDependents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public KycStep getCompletionStep() {
        return completionStep;
    }

    public void setCompletionStep(KycStep completionStep) {
        this.completionStep = completionStep;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public static class KycResponseBuilder {
        private Long id;
        private Long userId;
        private String fullName;
        private LocalDate dateOfBirth;
        private String gender;
        private String idType;
        private String idNumber;
        private String residenceAddress;
        private String businessDetails;
        private String employmentStatus;
        private String employerName;
        private String employerAddress;
        private String tinNumber;
        private String businessName;
        private String businessTinNumber;
        private String businessRegistrationNumber;
        private String incomeRange;
        private String incomeSource;
        private BigDecimal loanAmountRequested;
        private String loanPurpose;
        private Integer repaymentPeriodMonths;
        private String maritalStatus;
        private Integer numberOfDependents;
        private String status;
        private String rejectionReason;
        private KycProfile.KycStep completionStep;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime approvedAt;

        public KycResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public KycResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public KycResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public KycResponseBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public KycResponseBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public KycResponseBuilder idType(String idType) {
            this.idType = idType;
            return this;
        }

        public KycResponseBuilder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }

        public KycResponseBuilder residenceAddress(String residenceAddress) {
            this.residenceAddress = residenceAddress;
            return this;
        }

        public KycResponseBuilder businessDetails(String businessDetails) {
            this.businessDetails = businessDetails;
            return this;
        }

        public KycResponseBuilder employmentStatus(String employmentStatus) {
            this.employmentStatus = employmentStatus;
            return this;
        }

        public KycResponseBuilder employerName(String employerName) {
            this.employerName = employerName;
            return this;
        }

        public KycResponseBuilder employerAddress(String employerAddress) {
            this.employerAddress = employerAddress;
            return this;
        }

        public KycResponseBuilder tinNumber(String tinNumber) {
            this.tinNumber = tinNumber;
            return this;
        }

        public KycResponseBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public KycResponseBuilder businessTinNumber(String businessTinNumber) {
            this.businessTinNumber = businessTinNumber;
            return this;
        }

        public KycResponseBuilder businessRegistrationNumber(String businessRegistrationNumber) {
            this.businessRegistrationNumber = businessRegistrationNumber;
            return this;
        }

        public KycResponseBuilder incomeRange(String incomeRange) {
            this.incomeRange = incomeRange;
            return this;
        }

        public KycResponseBuilder incomeSource(String incomeSource) {
            this.incomeSource = incomeSource;
            return this;
        }

        public KycResponseBuilder loanAmountRequested(BigDecimal loanAmountRequested) {
            this.loanAmountRequested = loanAmountRequested;
            return this;
        }

        public KycResponseBuilder loanPurpose(String loanPurpose) {
            this.loanPurpose = loanPurpose;
            return this;
        }

        public KycResponseBuilder repaymentPeriodMonths(Integer repaymentPeriodMonths) {
            this.repaymentPeriodMonths = repaymentPeriodMonths;
            return this;
        }

        public KycResponseBuilder maritalStatus(String maritalStatus) {
            this.maritalStatus = maritalStatus;
            return this;
        }

        public KycResponseBuilder numberOfDependents(Integer numberOfDependents) {
            this.numberOfDependents = numberOfDependents;
            return this;
        }

        public KycResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public KycResponseBuilder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public KycResponseBuilder completionStep(KycProfile.KycStep completionStep) {

            this.completionStep = completionStep;
            return this;
        }

        public KycResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public KycResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public KycResponseBuilder approvedAt(LocalDateTime approvedAt) {
            this.approvedAt = approvedAt;
            return this;
        }

        public KycResponse build() {
            return new KycResponse(id, userId, fullName, dateOfBirth, gender, idType, idNumber, residenceAddress,
                    businessDetails, employmentStatus, employerName, employerAddress, tinNumber, businessName,
                    businessTinNumber, businessRegistrationNumber, incomeRange, incomeSource, loanAmountRequested,
                    loanPurpose, repaymentPeriodMonths, maritalStatus, numberOfDependents, status, rejectionReason,
                    completionStep, createdAt, updatedAt, approvedAt);
        }
    }
}
