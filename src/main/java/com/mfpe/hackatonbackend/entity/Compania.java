package com.mfpe.hackatonbackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "companies", schema = "dbo")
public class Compania implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ruc")
    private String ruc;

    @Column(name = "business_purpose")
    private String businessPurpose;

    @Column(name = "contribuyente_type")
    private String contribuyenteType;

    @Column(name = "rubro")
    private String rubro;

    @Column(name = "economic_activity")
    private String economicActivity;

    @Column(name = "constitution_date")
    private String constitutionDate;

    @Column(name = "address")
    private String address;

    @Column(name = "province_id")
    private String provinceId;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "email")
    private String email;

    @Column(name = "anual_sells_min")
    private BigDecimal minAnualSells;

    @Column(name = "anual_sells_max")
    private BigDecimal maxAnualSells;

    @Column(name = "temporal")
    private boolean temporal;

}