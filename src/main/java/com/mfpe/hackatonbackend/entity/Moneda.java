package com.mfpe.hackatonbackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "currencies", schema = "dbo")
public class Moneda implements Serializable {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;
}
