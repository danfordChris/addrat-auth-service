package com.pesa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_board_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditBoardScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    private BigDecimal score;

    private String grade;

    @Column(name = "loan_limit")
    private BigDecimal loanLimit;

    @Builder.Default
    private Boolean eligible = false;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;
}
