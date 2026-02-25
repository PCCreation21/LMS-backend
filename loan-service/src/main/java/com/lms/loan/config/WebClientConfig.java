package com.lms.loan.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final HeaderPropagationFilter headerPropagationFilter;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Bean
    public WebClient customerWebClient() {
        return WebClient.builder()
                .baseUrl(customerServiceUrl)
                .filter(headerPropagationFilter)
                .build();
    }
}
