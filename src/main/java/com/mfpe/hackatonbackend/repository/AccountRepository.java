package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Cuenta;
import com.mfpe.hackatonbackend.entity.SolicitudCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Cuenta, Integer> {

}
