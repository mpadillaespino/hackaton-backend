package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Integer> {
    Optional<OtpCode> findByCodeAndEntityIdAndEntityToEvaluate(String code, int entityId, String entityToEvaluate);
}
