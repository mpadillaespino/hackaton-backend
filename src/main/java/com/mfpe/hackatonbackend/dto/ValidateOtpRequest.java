package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateOtpRequest implements Serializable {
    
    private String entityToEvaluate;
    private int entityId;
    private String code;
    private int requestId = 0;

}
