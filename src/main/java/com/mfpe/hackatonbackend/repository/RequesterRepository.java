package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequesterRepository extends JpaRepository<Solicitante, Integer> {

}
