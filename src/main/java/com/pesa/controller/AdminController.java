package com.pesa.controller;

import com.pesa.common.pagination.PaginationParam;
import com.pesa.entity.AuditLog;
import com.pesa.entity.KycProfile;
import com.pesa.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.pesa.common.api.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    // ─── Auth (public) ────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        Map<String, Object> result = adminService.loginAdmin(request.email(), request.password());
        return ResponseEntity.ok(ApiResponses.success("Login successful", result));
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> stats = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponses.success("Dashboard data", stats));
    }

    // ─── Users ───────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @PaginationParam(defaultSortBy = "id", defaultSortDirection = Sort.Direction.DESC) Pageable pageable) {
        Page<Map<String, Object>> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponses.success("Users retrieved", users));
    }

    @PostMapping("/users/{userId}/kyc/decide")
    public ResponseEntity<?> decideKyc(
            @PathVariable Long userId,
            @RequestBody KycDecideRequest request,
            Authentication authentication) {
        Long adminId = (Long) authentication.getDetails();
        KycProfile profile = adminService.decideKyc(userId, request.action(), request.reason(), adminId);
        return ResponseEntity.ok(ApiResponses.success("KYC decision applied", profile));
    }

    @PostMapping("/users/{userId}/credit-score")
    public ResponseEntity<?> setCreditScore(
            @PathVariable Long userId,
            @RequestBody CreditScoreRequest request) {
        var score = adminService.setCreditScore(
            userId, request.score(), request.grade(),
            request.loanLimit(), request.eligible());
        return ResponseEntity.ok(ApiResponses.success("Credit score updated", score));
    }

    // ─── Audit Logs ───────────────────────────────────────────────────────────

    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @PaginationParam(defaultSize = 50, defaultSortBy = "createdAt", defaultSortDirection = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = adminService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponses.success("Audit logs retrieved", logs));
    }
}

record AdminLoginRequest(String email, String password) {}
record KycDecideRequest(String action, String reason) {}
record CreditScoreRequest(BigDecimal score, String grade, BigDecimal loanLimit, boolean eligible) {}
