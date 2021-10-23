package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Provincia, String> {

    List<Provincia> findAllByDepartamento_Id(String departamento_id);

}
