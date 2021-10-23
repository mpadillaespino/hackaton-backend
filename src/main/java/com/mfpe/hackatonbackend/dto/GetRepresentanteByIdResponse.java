package com.mfpe.hackatonbackend.dto;

import com.mfpe.hackatonbackend.entity.Compania;
import com.mfpe.hackatonbackend.entity.Representante;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetRepresentanteByIdResponse implements Serializable {

    private Representante representative;
    private Compania company;

}
