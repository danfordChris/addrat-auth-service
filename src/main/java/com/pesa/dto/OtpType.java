package com.pesa.dto;

public enum OtpType {

    LOGIN("login", 1),
    TRANSACTION("transaction", 2);

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