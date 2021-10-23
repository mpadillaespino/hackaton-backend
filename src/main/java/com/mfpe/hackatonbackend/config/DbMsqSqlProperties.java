package com.mfpe.hackatonbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mfpe.hackatonbackend.dbmsql")
public class DbMsqSqlProperties extends AbstractDbMsSqlProperties {
}

