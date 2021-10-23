package com.mfpe.hackatonbackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "account_requests", schema = "dbo")
public class SolicitudCuenta implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "requester_id")
    private int requesterId;

    @Column(name = "company_id")
    private int companyId;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "province_id")
    private String provinceId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "account_email")
    private String accountEmail;

}