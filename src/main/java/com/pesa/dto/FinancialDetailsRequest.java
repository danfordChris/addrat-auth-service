package com.pesa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDetailsRequest {
    private int bankId;
    private int branchId;
    private String accountNumber;
    private String sourceOfIncome;
    private String incomeRange;
}
