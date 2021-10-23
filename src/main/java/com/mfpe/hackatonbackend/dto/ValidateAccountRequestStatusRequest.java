package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateAccountRequestStatusRequest implements Serializable {

    private int requestId;

}
