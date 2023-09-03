package com.ead.authuser.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig { //configuração para acessar a api de cursos

    static final int TIMEOUT = 5000;

    @LoadBalanced //balanceamento de carga do spring cloud 'eureka'
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder
                .setConnectTimeout(Duration.ofMillis(TIMEOUT)) //circuit break resilience
                .setReadTimeout(Duration.ofMillis(TIMEOUT)) //circuit break resilience
                .build();
    }
}
