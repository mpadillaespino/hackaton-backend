package com.mfpe.hackatonbackend.config;

import lombok.Data;

@Data
public abstract class  AbstractDbMsSqlProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
