package com.lms.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${loan.service.url}")
    private String loanServiceUrl;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Bean("loanWebClient")
    public WebClient loanWebClient() {
        return WebClient.builder().baseUrl(loanServiceUrl).build();
    }

    @Bean("customerWebClient")
    public WebClient customerWebClient() {
        return WebClient.builder().baseUrl(customerServiceUrl).build();
    }
}
