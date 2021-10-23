package com.mfpe.hackatonbackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "account_request_approvers", schema = "dbo")
public class SolicitudCuentaAprob implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "request_id")
    private int requestId;

    @Column(name = "representative_id")
    private int representativeId;

    @Column(name = "approved")
    private boolean approved;

}