package com.mfpe.hackatonbackend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({DbMsqSqlProperties.class,MicroServiceProperties.class})
@Import({DbMsSqlConfiguration.class})
@ComponentScan(value = {"com.mfpe.hackatonbackend"})
public class MicroserviceConfiguration {

    @Bean
    public AWSCredentials getCredentials(MicroServiceProperties microServiceProperties){

        return new BasicAWSCredentials(microServiceProperties.getConfiguration().getSms().getAccessKey(),
                microServiceProperties.getConfiguration().getSms().getSecretKey());

    }

    @Bean
    public AmazonPinpoint getAmazonPinpoint(MicroServiceProperties microServiceProperties,
                                            AWSCredentials credentials){

        return AmazonPinpointClient
                .builder()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(microServiceProperties.getConfiguration().getSms().getRegion())
                .build();
    }


}