package com.pesa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import com.pesa.entity.KycProfile.KycStep;

public class KycRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "ID type is required")
    private String idType;

    @NotBlank(message = "ID number is required")
    private String idNumber;

    @NotBlank(message = "Residence address is required")
    private String residenceAddress;

    private String businessDetails;

    @NotBlank(message = "Marital status is required")
    private String maritalStatus;

    private Integer numberOfDependents;

    private KycStep step;

    public KycRequest() {
    }

    public KycRequest(String fullName, LocalDate dateOfBirth, String gender, String idType, String idNumber,
            String residenceAddress, String businessDetails, String maritalStatus, Integer numberOfDependents,
            KycStep step) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.idType = idType;
        this.idNumber = idNumber;
        this.residenceAddress = residenceAddress;
        this.businessDetails = businessDetails;
        this.maritalStatus = maritalStatus;
        this.numberOfDependents = numberOfDependents;
        this.step = step;
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

    public KycStep getStep() {
        return step;
    }

    public void setStep(KycStep step) {
        this.step = step;
    }
}
