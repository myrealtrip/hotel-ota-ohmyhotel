package com.myrealtrip.ohmyhotel.outbound.agent.common;

import com.myrealtrip.ohmyhotel.outbound.slack.sender.circuit.CircuitBreakerSlackSender;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerFactory {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerConfig defaultCircuitBreakerConfig;
    private final CircuitBreakerSlackSender circuitBreakerSlackSender;

    public CircuitBreakerFactory(CircuitBreakerRegistry circuitBreakerRegistry,
                                 CircuitBreakerConfig.Builder circuitBreakerConfigBuilder,
                                 CircuitBreakerSlackSender circuitBreakerSlackSender) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.defaultCircuitBreakerConfig = circuitBreakerConfigBuilder // application-srt-common.yml 에 디폴트 설정 정의되어 있음
            .ignoreExceptions() // TODO
            .build();
        this.circuitBreakerSlackSender = circuitBreakerSlackSender;
    }

    public CircuitBreaker create(String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName, defaultCircuitBreakerConfig);
        setEvent(circuitBreaker);
        return circuitBreaker;
    }

    public void setEvent(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher().onStateTransition(event -> circuitBreakerSlackSender.sendForStateTransition(circuitBreaker.getName(), circuitBreaker.getState()));
    }
}
