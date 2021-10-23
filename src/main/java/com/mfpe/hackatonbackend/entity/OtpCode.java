package com.mfpe.hackatonbackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "otpcodes", schema = "dbo")
public class OtpCode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "entity_to_evaluate")
    private String entityToEvaluate;

    @Column(name = "entity_id")
    private int entityId;

    @Column(name = "expire_time")
    private Date expireTime;

}