package com.pesa.service;

import com.pesa.common.exception.BadRequestException;
import com.pesa.common.exception.ForbiddenException;
import com.pesa.common.exception.NotFoundException;
import com.pesa.common.exception.UnauthorizedException;
import com.pesa.entity.*;
import com.pesa.repository.*;
import com.pesa.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final KycProfileRepository kycProfileRepository;
    private final CreditBoardScoreRepository creditBoardScoreRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    // ─── Admin Authentication ─────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> loginAdmin(String email, String password) {
        AdminUser admin = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!admin.getIsActive()) {
            throw new ForbiddenException("Account is disabled");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        admin.setLastLogin(LocalDateTime.now());
        adminUserRepository.save(admin);

        String token = tokenProvider.generateAccessToken(admin.getId(), admin.getEmail());

        log.info("Admin login: {}", email);

        return Map.of(
                "accessToken", token,
                "forcePasswordChange", !admin.getPasswordChanged(),
                "admin", Map.of("email", admin.getEmail(), "role", admin.getRole()));
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────

    public Map<String, Object> getDashboard() {
        Long totalUsers = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'", Long.class);
        Long pendingKyc = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM kyc_profiles WHERE status = 'PENDING'", Long.class);
        Long totalLoans = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM loans", Long.class);
        BigDecimal outstanding = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_amount_due), 0) FROM loans WHERE status IN ('DISBURSED','APPROVED')",
                BigDecimal.class);
        BigDecimal collected = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM loan_payments", BigDecimal.class);

        return Map.of(
                "totalUsers", totalUsers != null ? totalUsers : 0L,
                "totalLoans", totalLoans != null ? totalLoans : 0L,
                "portfolioOutstanding", outstanding != null ? outstanding : BigDecimal.ZERO,
                "totalCollected", collected != null ? collected : BigDecimal.ZERO,
                "pendingKyc", pendingKyc != null ? pendingKyc : 0L);
    }

    // ─── User Management ─────────────────────────────────────────────────────

    public Page<Map<String, Object>> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> {
            KycProfile kyc = kycProfileRepository.findByUserId(user.getId()).orElse(null);
            CreditBoardScore credit = creditBoardScoreRepository.findByUserId(user.getId()).orElse(null);

            Map<String, Object> summary = new java.util.LinkedHashMap<>();
            summary.put("id", user.getId());
            summary.put("phoneNumber", user.getPhoneNumber());
            summary.put("fullName", user.getFullName() != null ? user.getFullName() : "—");
            summary.put("status", user.getStatus());
            summary.put("createdAt", user.getCreatedAt());
            summary.put("kycStatus", kyc != null ? kyc.getStatus().name() : "NONE");
            summary.put("kycCompletionStep", kyc != null ? kyc.getCompletionStep() : 0);
            summary.put("kycProfileId", kyc != null ? kyc.getId() : null);
            summary.put("creditGrade", credit != null ? credit.getGrade() : "—");
            summary.put("loanLimit", credit != null ? credit.getLoanLimit() : BigDecimal.ZERO);
            summary.put("eligible", credit != null && credit.getEligible());
            return summary;
        });
    }

    // ─── KYC Management ──────────────────────────────────────────────────────

    @Transactional
    public KycProfile decideKyc(Long userId, String action, String reason, Long adminId) {
        KycProfile profile = kycProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("KYC profile not found for user: " + userId));

        String oldStatus = profile.getStatus().name();

        if ("APPROVE".equalsIgnoreCase(action)) {
            profile.setStatus(KycProfile.KycStatus.APPROVED);
            profile.setApprovedAt(LocalDateTime.now());
        } else if ("REJECT".equalsIgnoreCase(action)) {
            profile.setStatus(KycProfile.KycStatus.REJECTED);
            profile.setRejectionReason(reason);
        } else {
            throw new BadRequestException("Invalid action: " + action + ". Use APPROVE or REJECT.");
        }

        profile = kycProfileRepository.save(profile);

        auditLogRepository.save(AuditLog.builder()
                .userId(adminId)
                .action("KYC_" + action.toUpperCase())
                .entityType("KYC_PROFILE")
                .entityId(profile.getId())
                .oldValues("{\"status\":\"" + oldStatus + "\"}")
                .newValues("{\"status\":\"" + profile.getStatus().name() + "\"}")
                .build());

        log.info("KYC {} for userId={} by adminId={}", action, userId, adminId);
        return profile;
    }

    // ─── Audit Logs ───────────────────────────────────────────────────────────

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // ─── Credit Score Management ──────────────────────────────────────────────

    @Transactional
    public CreditBoardScore setCreditScore(Long userId, BigDecimal score, String grade,
            BigDecimal loanLimit, boolean eligible) {
        CreditBoardScore creditScore = creditBoardScoreRepository.findByUserId(userId)
                .orElse(CreditBoardScore.builder().userId(userId).build());

        creditScore.setScore(score);
        creditScore.setGrade(grade);
        creditScore.setLoanLimit(loanLimit);
        creditScore.setEligible(eligible);
        creditScore.setEvaluatedAt(LocalDateTime.now());

        CreditBoardScore saved = creditBoardScoreRepository.save(creditScore);

        auditLogRepository.save(AuditLog.builder()
                .action("CREDIT_SCORE_SET")
                .entityType("CREDIT_BOARD_SCORE")
                .entityId(userId)
                .newValues("{\"grade\":\"" + grade + "\",\"limit\":" + loanLimit + "}")
                .build());

        return saved;
    }
}
