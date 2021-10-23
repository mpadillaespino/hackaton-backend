package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApproveAccountRequest implements Serializable {

    private int representativeId;
    private int requestId;
    private String doi;
    private String powerVigency;
    private String representativeSign;

}
