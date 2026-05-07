package com.pesa.repository;

import com.pesa.entity.CreditBoardScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditBoardScoreRepository extends JpaRepository<CreditBoardScore, Long> {
    Optional<CreditBoardScore> findByUserId(Long userId);
}
