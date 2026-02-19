package com.sunny.ai.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter that injects X-Trace-Id and logs request/response metadata.
 */
@Slf4j
@Component
public class RequestTracingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Trace-Id", traceId)
                .build();

        long startTime = System.currentTimeMillis();

        log.info("[GATEWAY] Incoming request: {} {} | TraceId: {}",
                request.getMethod(), request.getURI(), traceId);

        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[GATEWAY] Response: {} | TraceId: {} | Duration: {}ms",
                            response.getStatusCode(), traceId, duration);
                });
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
