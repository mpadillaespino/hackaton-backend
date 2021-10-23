package com.mfpe.hackatonbackend.dto;

import com.mfpe.hackatonbackend.entity.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestSummaryResponse implements Serializable {

    private SolicitudCuenta request;
    private Compania company;
    private Solicitante requester;
    private Provincia province;
    private List<Representante> representatives;

}
