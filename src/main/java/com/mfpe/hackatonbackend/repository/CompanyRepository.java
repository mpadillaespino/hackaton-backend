package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Compania;
import com.mfpe.hackatonbackend.entity.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Compania, Integer> {

    Optional<Compania> findByRucAndTemporalFalse(String ruc);

}
