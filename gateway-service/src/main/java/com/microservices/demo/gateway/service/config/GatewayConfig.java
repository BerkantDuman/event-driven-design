package com.microservices.demo.gateway.service.config;

import com.microservices.demo.config.GatewayServiceConfigData;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class GatewayConfig {

    private final GatewayServiceConfigData gatewayServiceConfigData;

    private static final String HEADER_FOR_KEY_RESOLVER = "Authorization";

    public GatewayConfig(GatewayServiceConfigData gatewayServiceConfigData) {
        this.gatewayServiceConfigData = gatewayServiceConfigData;
    }

    @Bean(name = "authHeaderResolver")
    KeyResolver userKeyResolver(){
        return exchange -> Mono.just(Objects
                .requireNonNull(
                    exchange.getRequest().getHeaders().getFirst(HEADER_FOR_KEY_RESOLVER)
                )
        );
    }


    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer() {
        return factory -> {
            factory.configure(builder -> builder
                    .timeLimiterConfig(TimeLimiterConfig.custom()
                            .timeoutDuration(Duration.ofMillis(gatewayServiceConfigData.getTimeoutMs()))
                            .build())
                    .circuitBreakerConfig(CircuitBreakerConfig.custom()
                            .failureRateThreshold(gatewayServiceConfigData.getFailureRateThreshold())
                            .slowCallDurationThreshold(Duration.ofMillis(gatewayServiceConfigData.getSlowCallDurationThreshold()))
                            .slowCallRateThreshold(gatewayServiceConfigData.getSlowCallRateThreshold())
                            .permittedNumberOfCallsInHalfOpenState(gatewayServiceConfigData.getPermittedNumOfCallsInHalfOpenState())
                            .slidingWindowSize(gatewayServiceConfigData.getSlidingWindowSize())
                            .minimumNumberOfCalls(gatewayServiceConfigData.getMinNumberOfCalls())
                            .waitDurationInOpenState(Duration.ofMillis(gatewayServiceConfigData.getWaitDurationInOpenState()))
                            .build()
                    )
            );
        };
    }
}
