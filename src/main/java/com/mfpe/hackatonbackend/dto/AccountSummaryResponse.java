package com.mfpe.hackatonbackend.dto;

import com.mfpe.hackatonbackend.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccountSummaryResponse implements Serializable {

    private Cuenta account;
    private RequestSummaryResponse data;

}
