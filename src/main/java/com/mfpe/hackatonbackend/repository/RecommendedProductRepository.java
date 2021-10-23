package com.mfpe.hackatonbackend.repository;

import com.mfpe.hackatonbackend.entity.ProductoRecomendado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedProductRepository extends JpaRepository<ProductoRecomendado, String> {

}
