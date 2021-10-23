package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateOtpResponse implements Serializable {

    private boolean success;
    private String message;

}
