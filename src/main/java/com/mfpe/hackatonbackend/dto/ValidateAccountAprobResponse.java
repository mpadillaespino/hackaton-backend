package com.mfpe.hackatonbackend.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValidateAccountAprobResponse implements Serializable {

    private boolean success;
    private GetRepresentanteByIdResponse data;

}
