package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Representante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepresentantiveRepository extends JpaRepository<Representante, Integer> {

    void deleteAllByCompanyId(int companyId);
    List<Representante> findAllByCompanyId(int companyId);

}
