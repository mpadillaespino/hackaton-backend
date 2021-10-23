package com.mfpe.hackatonbackend.dto;

import com.mfpe.hackatonbackend.entity.Compania;
import com.mfpe.hackatonbackend.entity.Provincia;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetCompanyByRucResponse implements Serializable {

    private Compania company;
    private Provincia province;

}
