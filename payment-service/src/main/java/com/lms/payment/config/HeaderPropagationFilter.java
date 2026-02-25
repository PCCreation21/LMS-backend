package com.lms.payment.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;
import jakarta.servlet.http.HttpServletRequest;
@Component
public class HeaderPropagationFilter implements ExchangeFilterFunction {
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        ClientRequest.Builder builder = ClientRequest.from(request);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest currentRequest = attributes.getRequest();
            String username = currentRequest.getHeader("X-User-Name");
            String role = currentRequest.getHeader("X-User-Role");
            String permissions = currentRequest.getHeader("X-User-Permissions");
            String authHeader = currentRequest.getHeader("Authorization");

            if (username != null) builder.header("X-User-Name", username);
            if (role != null) builder.header("X-User-Role", role);
            if (permissions != null) builder.header("X-User-Permissions", permissions);
            if (authHeader != null) builder.header("Authorization", authHeader);
        }
        return next.exchange(builder.build());
    }
}