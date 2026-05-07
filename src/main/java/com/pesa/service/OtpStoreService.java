package com.pesa.service;

import com.pesa.common.exception.BadRequestException;
import com.pesa.common.exception.NotFoundException;
import com.pesa.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class OtpStoreService {

    private static final String KEY_PREFIX = "otp:";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_ATTEMPTS = "attempts";
    private static final String FIELD_USED = "used";
    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    @Value("${otp.ttl-minutes:10}")
    private long otpTtlMinutes;

    public void saveOtp(String phoneNumber, String code) {
        String key = key(phoneNumber);
        Map<Object, Object> values = new HashMap<>();
        values.put(FIELD_CODE, code);
        values.put(FIELD_ATTEMPTS, "0");
        values.put(FIELD_USED, "false");
        redisTemplate.opsForHash().putAll(key, values);
        redisTemplate.expire(key, Duration.ofMinutes(otpTtlMinutes));
    }

    public void verifyOtp(String phoneNumber, String code) {
        String key = key(phoneNumber);
        Map<Object, Object> otpData = redisTemplate.opsForHash().entries(key);
        if (otpData.isEmpty()) {
            throw new NotFoundException("No OTP found for this phone number");
        }

        boolean used = Boolean.parseBoolean(String.valueOf(otpData.getOrDefault(FIELD_USED, "false")));
        if (used) {
            throw new UnauthorizedException("OTP expired or already used");
        }

        String storedCode = String.valueOf(otpData.get(FIELD_CODE));
        int attempts = Integer.parseInt(String.valueOf(otpData.getOrDefault(FIELD_ATTEMPTS, "0")));

        if (!storedCode.equals(code)) {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                redisTemplate.delete(key);
                throw new UnauthorizedException("OTP attempts exceeded");
            }
            redisTemplate.opsForHash().put(key, FIELD_ATTEMPTS, String.valueOf(attempts));
            throw new BadRequestException("Invalid OTP");
        }

        redisTemplate.delete(key);
    }

    private String key(String phoneNumber) {
        return KEY_PREFIX + phoneNumber;
    }
}
