package com.mfpe.hackatonbackend.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SendOtpRequest implements Serializable {

    private String type;
    private String entityToEvaluate;
    private int entityId;
    private String emailOrPhoneNumber;

}
