package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateAccountAprobRequest implements Serializable {

    private int requestId;
    private int representativeId;

}
