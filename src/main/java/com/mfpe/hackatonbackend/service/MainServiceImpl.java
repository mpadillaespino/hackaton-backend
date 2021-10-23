package com.mfpe.hackatonbackend.service;


import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.model.*;
import com.mfpe.hackatonbackend.config.MicroServiceProperties;
import com.mfpe.hackatonbackend.dto.*;
import com.mfpe.hackatonbackend.entity.*;
import com.mfpe.hackatonbackend.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {

    private final JavaMailSender javaMailSender;
    private final AmazonPinpoint amazonPinpoint;
    private final MicroServiceProperties microServiceProperties;

    @Autowired
    private final RequesterRepository requesterRepository;
    @Autowired
    private final DepartmentRepository departmentRepository;
    @Autowired
    private final ProvinceRepository provinceRepository;

    @Autowired
    private final CurrencyRepository currencyRepository;
    @Autowired
    private final EconomicActivityRepository economicActivityRepository;
    @Autowired
    private final OtpCodeRepository otpCodeRepository;
    @Autowired
    private final CompanyRepository companyRepository;
    @Autowired
    private final RepresentantiveRepository representantiveRepository;
    @Autowired
    private final AccountRequestRepository accountRequestRepository;
    @Autowired
    private final AccountRequestAprobRepository accountRequestAprobRepository;
    @Autowired
    private final RecommendedProductRepository recommendedProductRepository;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final JavaMailSender emailSender;

    @Override
    public List<Departamento> findDepartamentos() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Provincia> findProvincias(String departamentId) {
        return provinceRepository.findAllByDepartamento_Id(departamentId);
    }

    @Override
    public Optional<Provincia> findProvinceById(String provinceId) {
        return provinceRepository.findById(provinceId);
    }

    @Override
    public List<Moneda> findMoneda() {
        return currencyRepository.findAll();
    }

    @Override
    public List<ProductoRecomendado> findRecommendedProducts() {
        return recommendedProductRepository.findAll();
    }

    @Override
    public List<ActividadEconomica> findActividadEconomica() {
        return economicActivityRepository.findAll();
    }

    @Override
    public void sendEmail(SendOtpRequest request) {
        sendByEmail(request);
    }

    @Override
    public void sendSms(SendOtpRequest request) {

        OtpCode otpCode = generateOtp(request.getEntityToEvaluate(), request.getEntityId());

        SMSMessage message = new SMSMessage();
        message.setMessageType(MessageType.TRANSACTIONAL);
        message.setBody("Tu codigo de verificacion es: " + otpCode.getCode());

        DirectMessageConfiguration directMessageConfiguration = new DirectMessageConfiguration();
        directMessageConfiguration.setSMSMessage(message);

        Map<String, AddressConfiguration> map = new HashMap<>();
        map.put(request.getEmailOrPhoneNumber(), new AddressConfiguration().withChannelType(ChannelType.SMS));

        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setAddresses(map);
        messageRequest.setMessageConfiguration(directMessageConfiguration);

        SendMessagesRequest request2 = new SendMessagesRequest();
        request2.setMessageRequest(messageRequest);
        request2.setApplicationId(microServiceProperties.getConfiguration().getSms().getApplicationId());

        amazonPinpoint.sendMessages(request2);
    }

    @Override
    public ValidateOtpResponse validateOtpCode(ValidateOtpRequest request) {
        Optional<OtpCode> otpCode = otpCodeRepository
                .findByCodeAndEntityIdAndEntityToEvaluate(
                        request.getCode(),
                        request.getEntityId(),
                        request.getEntityToEvaluate());

        ValidateOtpResponse response = new ValidateOtpResponse();
        response.setSuccess(false);
        response.setMessage("");


        if (otpCode.isEmpty()){
            response.setMessage("Código OTP inválido.");
            return response;
        }

        if (otpCode.get().getExpireTime().compareTo(Calendar.getInstance().getTime()) < 0){
            response.setMessage("Código OTP expirado.");
            return response;
        }

        // si era validacion de aprobacion de cuenta.
        if (request.getRequestId() != 0){
            Optional<SolicitudCuentaAprob> aprob = accountRequestAprobRepository
                    .findByApprovedFalseAndRequestIdAndRepresentativeId
                            (request.getRequestId(), request.getEntityId());

            if (aprob.isPresent()){
                aprob.get().setApproved(true);
                accountRequestAprobRepository.save(aprob.get());
            } else {
                response.setMessage("La solicitud ya fue aprobada.");
                return response;
            }
        }

        response.setSuccess(true);
        return response;
    }

    @Override
    public Solicitante saveRequester(Solicitante request) {
        return requesterRepository.save(request);
    }

    @Override
    public Optional<Compania> findCompanyByRuc(String ruc) {
        return companyRepository.findByRucAndTemporalFalse(ruc);
    }

    @Override
    public Optional<Compania> findCompanyById(int companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Compania saveCompany(Compania request) {
        request.setTemporal(true);
        return companyRepository.save(request);
    }

    @Transactional
    @Override
    public List<Representante> saveRepresentantes(int companyId, List<Representante> request) {
        representantiveRepository.deleteAllByCompanyId(companyId);
        return representantiveRepository.saveAll(request);
    }

    @Override
    public SolicitudCuenta saveSolicitud(SolicitudCuenta request) {
        // guardo solicitud de cuenta
        request.setCreationDate(new Date());
        SolicitudCuenta solicitud = accountRequestRepository.save(request);
        // guarda la empresa como fija.
        Optional<Compania> company = companyRepository.findById(request.getCompanyId());
        if (company.isPresent()){
            company.get().setTemporal(false);
            companyRepository.save(company.get());
        }

        // aprobaciones de representantes.
        List<Representante> representantes = representantiveRepository.findAllByCompanyId(request.getCompanyId());
        for (Representante rep: representantes) {

            //guardar pendiente aprob.
            SolicitudCuentaAprob sol = new SolicitudCuentaAprob();
            sol.setRequestId(solicitud.getId());
            sol.setRepresentativeId(rep.getId());
            sol.setApproved(false);
            accountRequestAprobRepository.save(sol);

            //enviar correo de aprob a cada representante.
            String mensaje = "<!DOCTYPE html\n" +
                    "    PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width\">\n" +
                    "    <meta name=\"format-detection\" content=\"telephone=no\">\n" +
                    "    <!--[if !mso]>\n" +
                    "\t\t\t\t\t\t  <!-->\n" +
                    "    <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:400,600,700,800,300&subset=latin\" rel=\"stylesheet\"\n" +
                    "        type=\"text/css\">\n" +
                    "    <!--<![endif]-->\n" +
                    "    <style type=\"text/css\">\n" +
                    "        * {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            font-family: \"OpenSans-Light\", \"Helvetica Neue\", \"Helvetica\", Calibri, Arial, sans-serif;\n" +
                    "            font-size: 100%;\n" +
                    "            line-height: 1.6;\n" +
                    "        }\n" +
                    "\n" +
                    "        #tdItem {\n" +
                    "            border: 1px;\n" +
                    "            border-style: solid;\n" +
                    "            border-color: #e6e6e6;\n" +
                    "            background-color: white;\n" +
                    "            font-size: 14px;\n" +
                    "            padding: 16px;\n" +
                    "            padding-left: 10px;\n" +
                    "        }\n" +
                    "\n" +
                    "        body {\n" +
                    "            -webkit-font-smoothing: antialiased;\n" +
                    "            -webkit-text-size-adjust: none;\n" +
                    "            width: 100% !important;\n" +
                    "            height: 100%;\n" +
                    "        }\n" +
                    "\n" +
                    "        a {\n" +
                    "            color: #348eda;\n" +
                    "        }\n" +
                    "\n" +
                    "        table.body-wrap {\n" +
                    "            width: 100%;\n" +
                    "            padding: 0px;\n" +
                    "            padding-top: 20px;\n" +
                    "            margin: 0px;\n" +
                    "        }\n" +
                    "\n" +
                    "        table.body-wrap .container {\n" +
                    "            border: 1px solid #f0f0f0;\n" +
                    "        }\n" +
                    "\n" +
                    "        p {\n" +
                    "            font-weight: normal;\n" +
                    "            font-size: 14px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            display: block !important;\n" +
                    "            max-width: 600px !important;\n" +
                    "            margin: 0 auto !important;\n" +
                    "            clear: both !important;\n" +
                    "        }\n" +
                    "\n" +
                    "        .body-wrap .container {\n" +
                    "            padding: 0px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 0 auto;\n" +
                    "            padding: 20px 33px 20px 37px;\n" +
                    "            display: block;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content table {\n" +
                    "            width: 100%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .preheader {\n" +
                    "            display: none !important;\n" +
                    "            visibility: hidden;\n" +
                    "            opacity: 0;\n" +
                    "            color: transparent;\n" +
                    "            height: 0;\n" +
                    "            width: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        body {\n" +
                    "            background-color: f6f6f6;\n" +
                    "        }\n" +
                    "\n" +
                    "        #f9 {\n" +
                    "            font-size: 10px;\n" +
                    "            line-height: 12px;\n" +
                    "            font-weight: normal;\n" +
                    "            font-family: \"OpenSans\", helvetica, sans-serif;\n" +
                    "            color: #535352;\n" +
                    "            text-align: justify;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <span class=\"preheader\">\n" +
                    "    </span>\n" +
                    "\n" +
                    "    <table class=\"body-wrap\" width=\"600\">\n" +
                    "        <tr>\n" +
                    "            <td class=\"container\" bgcolor=\"#FFFFFF\">\n" +
                    "\n" +
                    "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"contentwrapper\" width=\"600\">\n" +
                    "                    <tr>\n" +
                    "                        <td>\n" +
                    "                            <div class=\"content\">\n" +
                    "                                <table class=\"content-message\" cellspacing=\"4\">\n" +
                    "                                    <tr>\n" +
                    "                                        <td align=\"center\" colspan=\"2\">\n" +
                    "                                            <a href=\"https://www.cmacica.com.pe/\">\n" +
                    "                                                <img src=\"https://plataforma.hackathonbbva.com/statics/img/hackbbva2021-logo.png\"\n" +
                    "                                                    width=\"100\">\n" +
                    "                                            </a>\n" +
                    "                                        </td>\n" +
                    "                                    </tr>\n" +
                    "                                    <tr></tr>\n" +
                    "                                    <tr>\n" +
                    "                                        <td style=\"background-color: white;font-size: 20px;\" align=\"center\" colspan=\"2\">\n" +
                    "                                            <b>Hackaton BBVA 2021</b><br>\n" +
                    "                                            <b style=\"background-color: white; font-size: 16px;\">Aprobación de solicitud de cuenta</b>\n" +
                    "                                        </td>\n" +
                    "                                    </tr>\n" +
                    "                                    <tr></tr>\n" +
                    "                                    <tr></tr>\n" +
                    "                                    <tr>\n" +
                    "                                        <td id=\"tdItem\" align=\"center\" colspan=\"2\">\n" +
                    "                                            ¡Hola! Para aprobar la solicitud de cuenta ingresa <a href=\"ENLACEALAWEB\" target=\"_blank\">aquí</a>\n" +
                    "                                        </td>\n" +
                    "                                    </tr>\n" +
                    "                                </table>\n" +
                    "                                <p style=\"font-size: 12px;padding-left:4px;\">Este correo es de uso informativo por favor no responder.</p>\n" +
                    "                            </div>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "                    <tr>\n" +
                    "                        <td>\n" +
                    "                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                    "                                <tr>\n" +
                    "                                    <td width=\"25\"></td>\n" +
                    "                                    <td id=\"f9\">\n" +
                    "                                        Aviso de confidencialidad:<br> Este correo electrónico y/o material adjunto es para uso exclusivo de la persona o entidad a la expresamente se le ha enviado,\n" +
                    "                                        cualquier otro uso contraviene las políticas de la HACKATON BBVA 2021. Si usted no es el destinatario legítimo del mismo, por favor\n" +
                    "                                        repórtelo inmediatamente al remitente del correo y bórrelo. Cualquier revisión, retrasmisión, difusión o cualquier otro uso de\n" +
                    "                                        este correo por personas o entidades distintas a las del destinatario legítimo, queda expresamente prohibido. En tal sentido,\n" +
                    "                                        nada de lo señalado en esta comunicación podrá ser interpretado como una recomendación sobre los riesgos o ventajas económicas,\n" +
                    "                                        legales, contables o tributarias, o sobre las consecuencias de realizar o no determinada transacción.\n" +
                    "                                    </td>\n" +
                    "                                    <td width=\"25\"></td>\n" +
                    "                                </tr>\n" +
                    "                                <tr>\n" +
                    "                                    <td colspan=\"3\">&nbsp;</td>\n" +
                    "                                </tr>\n" +
                    "                            </table>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "                </table>\n" +
                    "            </td>\n" +
                    "            <td></td>\n" +
                    "        </tr>\n" +
                    "    </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            String enlace = microServiceProperties.getConfiguration().getApprobeurl()
                    + generatApprobeUrl(Integer.toString(rep.getId()), Integer.toString(solicitud.getId()));
            mensaje = mensaje.replace("ENLACEALAWEB", enlace);
            sendByEmailHtml(rep.getEmail(), mensaje, "Aprobación de cuenta BBVA - HACKATON");
        }

        return solicitud;
    }

    @Override
    public Optional<SolicitudCuenta> findSolicitud(int requestId) {
        return accountRequestRepository.findById(requestId);
    }

    @Override
    public Optional<Representante> findRepresentanteById(int representativeId) {
        return representantiveRepository.findById(representativeId);
    }

    @Override
    public ValidateAccountAprobResponse validateAccountRequestStatus(ValidateAccountAprobRequest request) {
        ValidateAccountAprobResponse response = new ValidateAccountAprobResponse();
        response.setSuccess(false);

        List<SolicitudCuentaAprob> pendingApprobs = accountRequestAprobRepository
                .findAllByApprovedFalseAndRequestId(request.getRequestId());

        if (pendingApprobs.size() == 0) {
            return response;
        }

        Optional<SolicitudCuentaAprob> aprob = accountRequestAprobRepository
                .findByApprovedFalseAndRequestIdAndRepresentativeId
                        (request.getRequestId(), request.getRepresentativeId());

        if (aprob.isEmpty()) {
            return response;
        }

        Optional<Representante> representanteById = findRepresentanteById(request.getRepresentativeId());
        GetRepresentanteByIdResponse rep = new GetRepresentanteByIdResponse();
        if (representanteById.isPresent()){
            rep.setRepresentative(representanteById.get());
            Optional<Compania> companyById = findCompanyById(representanteById.get().getCompanyId());
            companyById.ifPresent(compania -> {
                rep.setCompany(compania);
                response.setSuccess(true);
                response.setData(rep);
            });
        }

        return response;
    }

    @Override
    public ApproveAccountResponse approbeAccountRequest(ApproveAccountRequest request) {
        ApproveAccountResponse response = new ApproveAccountResponse();
        response.setSuccess(false);

        Optional<SolicitudCuentaAprob> aprob = accountRequestAprobRepository
                .findByApprovedFalseAndRequestIdAndRepresentativeId
                        (request.getRequestId(), request.getRepresentativeId());

        if (aprob.isPresent()){
            aprob.get().setApproved(true);
            accountRequestAprobRepository.save(aprob.get());

            // veo si aun hay pendientes.
            List<SolicitudCuentaAprob> pendingApprobs = accountRequestAprobRepository
                    .findAllByApprovedFalseAndRequestId(request.getRequestId());

            if (pendingApprobs.size() > 0) {
                response.setSuccess(true);
                return response;
            }
        } else {
            return response;
        }

        response.setSuccess(true);
        AccountSummaryResponse accountSummary = getAccountSummaryResponse(request.getRequestId());
        response.setDataCuenta(accountSummary);
        return response;
    }

    @Override
    public RequestSummaryResponse getSolicitudSummary(int requestId) {
        return getRequestSummaryResponse(requestId);
    }

    private RequestSummaryResponse getRequestSummaryResponse(int requestId) {
        RequestSummaryResponse response = new RequestSummaryResponse();
        Optional<SolicitudCuenta> solicitud = accountRequestRepository.findById(requestId);

        if (solicitud.isPresent()){
            response.setRequest(solicitud.get());

            Optional<Compania> company = companyRepository.findById(solicitud.get().getCompanyId());
            company.ifPresent(response::setCompany);

            Optional<Solicitante> requester = requesterRepository.findById(solicitud.get().getRequesterId());
            requester.ifPresent(response::setRequester);

            Optional<Provincia> province = provinceRepository.findById(solicitud.get().getProvinceId());
            province.ifPresent(response::setProvince);

            List<SolicitudCuentaAprob> aprobs = accountRequestAprobRepository.findAllByRequestId(requestId);

            ArrayList<Representante> reps = new ArrayList<>();

            for (SolicitudCuentaAprob aprob: aprobs ) {
                Optional<Representante> representante = representantiveRepository.findById(aprob.getRepresentativeId());
                representante.ifPresent(reps::add);
            }
            response.setRepresentatives(reps);
        }

        return response;
    }

    @Override
    public AccountSummaryResponse generateAccount(int requestId) {
        return getAccountSummaryResponse(requestId);

    }

    private AccountSummaryResponse getAccountSummaryResponse(int requestId) {
        Cuenta cuenta = new Cuenta();
        long accountNum = generateRandomNumber(18);
        String acc = Long.toString(accountNum);
        cuenta.setAccountNumber(formatAccountNumber(acc));
        cuenta.setBalance(BigDecimal.ZERO);
        cuenta.setCreationDate(new Date());
        cuenta.setRequestId(requestId);
        Cuenta cuentaSave = accountRepository.save(cuenta);

        AccountSummaryResponse response = new AccountSummaryResponse();
        response.setAccount(cuentaSave);
        response.setData(getRequestSummaryResponse(requestId));

        return response;
    }

    private OtpCode generateOtp(String entityToEvaluate, int entityId) {
        Random rand = new Random();
        Calendar currentTimeNow = Calendar.getInstance();
        currentTimeNow.add(Calendar.MINUTE, 5);

        OtpCode otp = new OtpCode();
        otp.setCode(String.format("%04d", rand.nextInt(10000)));
        otp.setEntityToEvaluate(entityToEvaluate);
        otp.setEntityId(entityId);
        otp.setExpireTime(currentTimeNow.getTime());
        otpCodeRepository.save(otp);
        return otp;
    }


    private void sendByEmail(SendOtpRequest request) {
        OtpCode otpCode = generateOtp(request.getEntityToEvaluate(), request.getEntityId());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@hackaton.com");
        message.setTo(request.getEmailOrPhoneNumber());
        message.setSubject("Hackaton OTP Code - BBVA");
        message.setText("Tu codigo de verificacion es: " + otpCode.getCode());
        emailSender.send(message);
    }

    private void sendByEmailHtml(String destiny, String messageHtml, String subject) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject(subject);
            message.setFrom("noreply@hackaton.com");
            message.setTo(destiny);
            message.setText(messageHtml, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private long generateRandomNumber(int n){
        double tenToN = Math.pow(10, n),
                tenToNMinus1 = Math.pow(10, n-1);
        return (long) (Math.random() * (tenToN - tenToNMinus1) + tenToNMinus1);
    }

    private String formatAccountNumber(String accountNumber){
        return new StringBuilder(accountNumber)
                .insert(3, "-")
                .insert(6, "-")
                .insert(10, "-")
                .insert(12, "-")
                .toString();
    }

    private String generatApprobeUrl(String repId, String reqId){
        String repIdbase64 = Base64.getEncoder().encodeToString(repId.getBytes(StandardCharsets.UTF_8));
        String reqIdbase64 = Base64.getEncoder().encodeToString(reqId.getBytes(StandardCharsets.UTF_8));
        String preUrl = "r2d2=" + repIdbase64 + "&bb8=" + reqIdbase64;
        String preUrlBase64 = Base64.getEncoder().encodeToString(preUrl.getBytes(StandardCharsets.UTF_8));
        return "?approve=" + preUrlBase64;
    }

}
