package com.pesa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status")
})
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    private String fullName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private IdType idType;

    private String idNumber;

    private String residenceAddress;

    private String businessDetails;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    private String employerName;

    private String employerAddress;

    private String tinNumber;

    private String businessName;

    private String businessTinNumber;

    private String businessRegistrationNumber;

    private String incomeRange;

    @Enumerated(EnumType.STRING)
    private IncomeSource incomeSource;

    private BigDecimal loanAmountRequested;

    private String loanPurpose;

    private Integer repaymentPeriodMonths;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private Integer numberOfDependents;

    @Enumerated(EnumType.STRING)
    private KycStatus status = KycStatus.PENDING;

    private String rejectionReason;

    private KycStep completionStep = KycStep.PERSONAL_INFO;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime approvedAt;

    public KycProfile() {
    }

    public KycProfile(Long userId, String fullName, LocalDate dateOfBirth, Gender gender, IdType idType,
            String idNumber, String residenceAddress, String businessDetails, MaritalStatus maritalStatus,
            Integer numberOfDependents) {
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
        this.status = KycStatus.PENDING;
        this.completionStep = KycStep.PERSONAL_INFO;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
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

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
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

    public IncomeSource getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(IncomeSource incomeSource) {
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

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getNumberOfDependents() {
        return numberOfDependents;
    }

    public void setNumberOfDependents(Integer numberOfDependents) {
        this.numberOfDependents = numberOfDependents;
    }

    public KycStatus getStatus() {
        return status;
    }

    public void setStatus(KycStatus status) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum KycStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }

    public enum IdType {
        PASSPORT, NATIONAL_ID, DRIVER_LICENSE
    }

    public enum EmploymentStatus {
        EMPLOYED("EMPLOYED", "Employed"),
        SELF_EMPLOYED("SELF_EMPLOYED", "Self-Employed"),
        UNEMPLOYED("UNEMPLOYED", "Unemployed");

        private final String value;
        private final String label;

        EmploymentStatus(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum IncomeSource {
        SALARY("SALARY"),
        BUSINESS("BUSINESS"),
        AGRICULTURE("AGRICULTURE"),
        RENTAL("RENTAL"),
        REMITTANCE("REMITTANCE"),
        OTHER("OTHER");

        private final String value;

        IncomeSource(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return value.replace('_', ' ');
        }
    }

    public enum KycStep {
        PERSONAL_INFO("Personal Information", 1),
        EMPLOYMENT_INFO("Employment Information", 2),
        FINANCIAL_INFO("Financial Information", 3),
        APPROVED("Approved", 4);

        private final String description;
        private final int stepNumber;

        KycStep(String description, int stepNumber) {
            this.description = description;
            this.stepNumber = stepNumber;
        }

        public String getDescription() {
            return description;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public static KycStep fromInteger(int step) {

            for (KycStep kycStep : KycStep.values()) {

                if (kycStep.getStepNumber() == step) {

                    return kycStep;

                }

            }

            throw new IllegalArgumentException("Invalid KYC step: " + step);

        }
    }

}
