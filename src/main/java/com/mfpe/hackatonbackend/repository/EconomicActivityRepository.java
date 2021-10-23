package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.ActividadEconomica;
import com.mfpe.hackatonbackend.entity.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomicActivityRepository extends JpaRepository<ActividadEconomica, String> {

}
