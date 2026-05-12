package com.pesa.service;

import com.pesa.common.exception.BadRequestException;
import com.pesa.common.exception.NotFoundException;
import com.pesa.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpStoreService {

    @PostConstruct
    public void init() {
        log.info("OtpStoreService initialized, testing Redis connection...");
        try {
            String testKey = "test-connection-" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "test", Duration.ofSeconds(1));
            redisTemplate.delete(testKey);
            log.info("✓ Redis connection test successful");
        } catch (Exception e) {
            log.error("✗ Redis connection test failed", e);
            throw new RuntimeException("Redis not available at startup", e);
        }
    }

    private static final String KEY_PREFIX = "otp:";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_ATTEMPTS = "attempts";
    private static final String FIELD_USED = "used";
    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    @Value("${otp.ttl-minutes:10}")
    private long otpTtlMinutes;

    public void saveOtp(String phoneNumber, String code) {
        try {
            log.debug("Saving OTP for phone: {}", phoneNumber);
            String key = key(phoneNumber);
            Map<Object, Object> values = new HashMap<>();
            values.put(FIELD_CODE, code);
            values.put(FIELD_ATTEMPTS, "0");
            values.put(FIELD_USED, "false");
            log.debug("About to call redisTemplate.opsForHash().putAll()");
            redisTemplate.opsForHash().putAll(key, values);
            log.debug("RedisTemplate putAll() successful");
            redisTemplate.expire(key, Duration.ofMinutes(otpTtlMinutes));
            log.debug("OTP saved successfully in Redis");
        } catch (Exception e) {
            log.error("Redis Connection Exception in saveOtp", e);
            log.error("Exception Type: {}, Message: {}", e.getClass().getName(), e.getMessage());
            throw new RuntimeException("Unable to connect to Redis: " + e.getMessage(), e);
        }
    }

    public void verifyOtp(String phoneNumber, String code) {
        String key = key(phoneNumber);
        Map<Object, Object> otpData = redisTemplate.opsForHash().entries(key);

        log.debug("Verifying OTP - Phone: {}, Key: {}, Redis Data: {}", phoneNumber, key, otpData);

        if (otpData.isEmpty()) {
            log.warn("No OTP found in Redis for key: {}", key);
            throw new NotFoundException("No OTP found for this phone number");
        }

        boolean used = Boolean.parseBoolean(String.valueOf(otpData.getOrDefault(FIELD_USED, "false")));
        if (used) {
            throw new UnauthorizedException("OTP expired or already used");
        }

        String storedCode = String.valueOf(otpData.get(FIELD_CODE));
        int attempts = Integer.parseInt(String.valueOf(otpData.getOrDefault(FIELD_ATTEMPTS, "0")));

        log.debug("OTP Comparison - Stored: '{}' (length: {}), Provided: '{}' (length: {})",
            storedCode, storedCode.length(), code, code.length());

        if (!storedCode.equals(code)) {
            attempts++;
            log.warn("OTP mismatch - attempts now: {}/{}", attempts, MAX_ATTEMPTS);
            if (attempts >= MAX_ATTEMPTS) {
                redisTemplate.delete(key);
                throw new UnauthorizedException("OTP attempts exceeded");
            }
            redisTemplate.opsForHash().put(key, FIELD_ATTEMPTS, String.valueOf(attempts));
            throw new BadRequestException("Invalid OTP");
        }

        log.info("OTP verified successfully for phone: {}", phoneNumber);
        redisTemplate.delete(key);
    }

    private String key(String phoneNumber) {
        return KEY_PREFIX + phoneNumber;
    }
}
