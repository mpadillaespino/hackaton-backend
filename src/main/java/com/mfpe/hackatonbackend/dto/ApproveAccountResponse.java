package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApproveAccountResponse implements Serializable {

    private boolean success;
    private AccountSummaryResponse dataCuenta;

}
