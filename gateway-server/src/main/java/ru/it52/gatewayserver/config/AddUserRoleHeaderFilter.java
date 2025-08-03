package ru.it52.gatewayserver.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AddUserRoleHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .flatMap(authentication -> {
                    if (authentication == null || authentication.getDetails() == null) {
                        return chain.filter(exchange);
                    }

                    Object roleObj = authentication.getDetails();
                    String role = roleObj != null ? roleObj.toString() : "";

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Role", role)
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }).switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        // фильтр должен быть после аутентификации
        return -1;
    }
}
