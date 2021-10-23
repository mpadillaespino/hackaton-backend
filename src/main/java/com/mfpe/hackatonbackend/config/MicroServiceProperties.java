package com.mfpe.hackatonbackend.config;

import com.sun.istack.NotNull;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;



@ConfigurationProperties("mfpe.hackatonbackend")
@Validated
@Data
public class MicroServiceProperties {

    private Configuration configuration;

    @Data
    static public class Configuration {

        @NotNull
        private String approbeurl;

        @NotNull
        private Sms sms;
    }


    @Data
    static public class Sms {
        private String region;
        private String applicationId;
        private String accessKey;
        private String secretKey;
    }


}
