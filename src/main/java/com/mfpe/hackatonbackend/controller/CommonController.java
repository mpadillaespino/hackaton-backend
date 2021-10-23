package com.mfpe.hackatonbackend.controller;

import com.mfpe.hackatonbackend.dto.*;
import com.mfpe.hackatonbackend.entity.*;
import com.mfpe.hackatonbackend.service.MainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/common/")
@Slf4j
public class CommonController {

    @Autowired
    private final MainService mainService;

    @GetMapping("/departaments")
    public HttpEntity<List<Departamento>> getDepartments () {
        return ResponseEntity.ok(mainService.findDepartamentos());
    }

    @GetMapping("/departaments/{departmentId}/provinces")
    public HttpEntity<List<Provincia>> getProvinces (@PathVariable("departmentId") String departmentId) {
        return ResponseEntity.ok(mainService.findProvincias(departmentId));
    }

    @GetMapping("/currencies")
    public HttpEntity<List<Moneda>> getCurrencies() {
        return ResponseEntity.ok(mainService.findMoneda());
    }

    @GetMapping("/recommended-products")
    public HttpEntity<List<ProductoRecomendado>> getRecommendedProducts() {
        return ResponseEntity.ok(mainService.findRecommendedProducts());
    }

    @GetMapping("/economic-activities")
    public HttpEntity<List<ActividadEconomica>> getEconomicActivities() {
        return ResponseEntity.ok(mainService.findActividadEconomica());
    }

    @PostMapping("/send-otp")
    public HttpEntity<Void> sendOtp(@RequestBody SendOtpRequest request) {

        if (request.getType().equals("EMAIL")){
            mainService.sendEmail(request);
        } else {
            mainService.sendSms(request);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate-otp")
    public HttpEntity<ValidateOtpResponse> validateOtp(@RequestBody ValidateOtpRequest request) {
        ValidateOtpResponse response = mainService.validateOtpCode(request);
        if (response.getMessage().isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/validate-account-request")
    public HttpEntity<ValidateAccountAprobResponse> validateAccountRequestStatus
            (@RequestBody ValidateAccountAprobRequest request) {
        return ResponseEntity.ok(mainService.validateAccountRequestStatus(request));
    }

    @PostMapping("/requester")
    public HttpEntity<Solicitante> saveRequester(@RequestBody Solicitante request) {
        return ResponseEntity.ok(mainService.saveRequester(request));
    }

    @PostMapping("/account-request")
    public HttpEntity<SolicitudCuenta> saveAccountRequest(@RequestBody SolicitudCuenta request) {
        return ResponseEntity.ok(mainService.saveSolicitud(request));
    }

    @PostMapping("/account-request-approve")
    public HttpEntity<ApproveAccountResponse> approbeAccountRequest(@RequestBody ApproveAccountRequest request) {
        return ResponseEntity.ok(mainService.approbeAccountRequest(request));
    }

    @PostMapping("/account-generate")
    public HttpEntity<AccountSummaryResponse> saveAccount(@RequestBody ValidateAccountRequestStatusRequest request) {
        Optional<SolicitudCuenta> solicitud = mainService.findSolicitud(request.getRequestId());

        if (solicitud.isPresent()){
            return ResponseEntity.ok(mainService.generateAccount(request.getRequestId()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account-request/{requestId}")
    public HttpEntity<RequestSummaryResponse> saveAccountRequest(@PathVariable("requestId") int requestId) {
        return ResponseEntity.ok(mainService.getSolicitudSummary(requestId));
    }

    @GetMapping("/company")
    public HttpEntity<GetCompanyByRucResponse> getCompanyByRuc(@RequestParam("ruc") String ruc) {
        Optional<Compania> companyByRuc = mainService.findCompanyByRuc(ruc);
        GetCompanyByRucResponse response = new GetCompanyByRucResponse();
        if (companyByRuc.isPresent()){
            response.setCompany(companyByRuc.get());
            Optional<Provincia> provincia = mainService.findProvinceById(companyByRuc.get().getProvinceId());
            provincia.ifPresent(response::setProvince);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/company")
    public HttpEntity<Compania> saveCompany(@RequestBody Compania request) {
        return ResponseEntity.ok(mainService.saveCompany(request));
    }

    @PostMapping("/company/{companyId}/representatives")
    public HttpEntity<List<Representante>> saveCompany(@PathVariable("companyId") int companyId,
                                            @RequestBody List<Representante> request) {
        return ResponseEntity.ok(mainService.saveRepresentantes(companyId, request));
    }

}