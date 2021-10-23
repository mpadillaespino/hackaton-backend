package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Departamento, String> {

}
