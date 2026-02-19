package com.sunny.ai.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declarative route configuration for the API Gateway.
 * Routes external traffic to microservices via service-discovery (lb://).
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Document Ingestion Service
                .route("document-ingestion-service", r -> r
                        .path("/api/v1/documents/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                                .circuitBreaker(c -> c
                                        .setName("document-ingestion-cb")
                                        .setFallbackUri("forward:/fallback/document-ingestion")))
                        .uri("lb://document-ingestion-service"))

                // Embedding Service
                .route("embedding-service", r -> r
                        .path("/api/v1/embeddings/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                                .circuitBreaker(c -> c
                                        .setName("embedding-service-cb")
                                        .setFallbackUri("forward:/fallback/embedding")))
                        .uri("lb://embedding-service"))

                // Query Service
                .route("query-service", r -> r
                        .path("/api/v1/queries/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                                .requestRateLimiter(rl -> rl
                                        .setRateLimiter(redisRateLimiter()))
                                .circuitBreaker(c -> c
                                        .setName("query-service-cb")
                                        .setFallbackUri("forward:/fallback/query")))
                        .uri("lb://query-service"))

                // AI Governance Service
                .route("ai-governance-service", r -> r
                        .path("/api/v1/governance/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                                .circuitBreaker(c -> c
                                        .setName("governance-service-cb")
                                        .setFallbackUri("forward:/fallback/governance")))
                        .uri("lb://ai-governance-service"))

                .build();
    }

    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter redisRateLimiter() {
        // 10 requests/second, burst of 20
        return new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(10, 20, 1);
    }
}
