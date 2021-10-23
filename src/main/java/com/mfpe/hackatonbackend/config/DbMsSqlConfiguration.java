package com.mfpe.hackatonbackend.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class DbMsSqlConfiguration {
    private final AbstractDbMsSqlProperties properties;

    public DbMsSqlConfiguration(AbstractDbMsSqlProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .username(this.properties.getUsername())
                .password(this.properties.getPassword())
                .url(this.properties.getUrl())
                .driverClassName(this.properties.getDriverClassName())
                .build();
    }
}