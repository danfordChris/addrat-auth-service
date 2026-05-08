package com.pesa.dto;

public enum OtpType {
    REGISTRATION("REGISTRATION", 1),
    LOGIN("LOGIN", 2),
    TRANSACTION("TRANSACTION", 3),
    PASSWORD_RESET("PASSWORD_RESET", 4);

    private final String value;
    private final int code;

    OtpType(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }
}