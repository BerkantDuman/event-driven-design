package com.microservices.demo.gateway.service.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteLocator {

    private KeyResolver keyResolver;

    @Bean
    RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(5, 10);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("first-microservice",r -> r.path("/elastic-query-service/**")
                        .filters(filter -> {
                            filter.rewritePath("/elastic-query-service/(?<path>.*)", "/${path}");
                            filter.requestRateLimiter(s-> {
                                s.setRateLimiter(redisRateLimiter());
                                s.setKeyResolver(keyResolver);
                            });
                            filter.circuitBreaker(c -> {
                                c.setName("queryServiceCircuitBreaker");
                                c.setFallbackUri("forward:/fallback/query-fallback");
                            });
                            return filter;
                        })
                        //.filters(f-> f.rewritePath("/elastic-query-service/(?<path>.*)", "/${path}"))
                        .uri("lb://elastic-query-service"))
                .build();
    }
}
