package com.myrealtrip.ohmyhotel.outbound.agent.mrt.unionstay;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.unionstay.dto.hotelota.switching.UnionstaySwitchKey;
import com.myrealtrip.unionstay.dto.hotelota.switching.UnionstaySwitchKey.UnionstaySwitchValue;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class UnionstaySwitchAgent {

    public static final String URI = "/v1/switch";
    public static final String UNIONSTAY_SWITCH = "Unionstay Switch";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public UnionstaySwitchAgent(@Qualifier("unionstaySwitchWebClient") WebClient unionstaySwitchWebClient,
                                CircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = unionstaySwitchWebClient;
        this.circuitBreaker = circuitBreakerFactory.create(UNIONSTAY_SWITCH);
    }

    public void updateSwitch(UnionstaySwitchKey switchKey, UnionstaySwitchValue value) {
        updateSwitchMono(switchKey, value).block();
    }

    public Mono<Void> updateSwitchMono(UnionstaySwitchKey switchKey, UnionstaySwitchValue value) {
        return webClient.put()
            .uri(URI, uriBuilder -> uriBuilder
                .queryParam("key", switchKey.name())
                .queryParam("value", value.name())
                .build())
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Void.class))
            .transform(CircuitBreakerOperator.of(circuitBreaker));
    }
}
