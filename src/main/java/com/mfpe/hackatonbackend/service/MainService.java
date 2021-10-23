package com.mfpe.hackatonbackend.service;

import com.mfpe.hackatonbackend.dto.*;
import com.mfpe.hackatonbackend.entity.*;

import java.util.List;
import java.util.Optional;

public interface MainService {

    List<Departamento> findDepartamentos();
    List<Provincia> findProvincias(String departamentId);
    Optional<Provincia> findProvinceById(String provinceId);
    List<Moneda> findMoneda();
    List<ProductoRecomendado> findRecommendedProducts();
    List<ActividadEconomica> findActividadEconomica();
    void sendEmail(SendOtpRequest request);
    void sendSms(SendOtpRequest request);
    ValidateOtpResponse validateOtpCode(ValidateOtpRequest request);
    Solicitante saveRequester(Solicitante request);
    Optional<Compania> findCompanyByRuc(String ruc);
    Optional<Compania> findCompanyById(int companyId);
    Compania saveCompany(Compania request);
    List<Representante> saveRepresentantes(int companyId, List<Representante> request);
    SolicitudCuenta saveSolicitud(SolicitudCuenta request);
    Optional<SolicitudCuenta> findSolicitud(int requestId);
    Optional<Representante> findRepresentanteById(int representativeId);
    ValidateAccountAprobResponse validateAccountRequestStatus(ValidateAccountAprobRequest request);
    ApproveAccountResponse approbeAccountRequest(ApproveAccountRequest request);
    RequestSummaryResponse getSolicitudSummary(int requestId);
    AccountSummaryResponse generateAccount(int requestId);
}
