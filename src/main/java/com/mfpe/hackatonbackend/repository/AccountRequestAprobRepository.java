package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.SolicitudCuentaAprob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRequestAprobRepository extends JpaRepository<SolicitudCuentaAprob, Integer> {

    Optional<SolicitudCuentaAprob> findByApprovedFalseAndRequestIdAndRepresentativeId
            (int requestId, int representativeId);

    List<SolicitudCuentaAprob> findAllByApprovedFalseAndRequestId(int requestId);
    List<SolicitudCuentaAprob> findAllByRequestId(int requestId);

}
