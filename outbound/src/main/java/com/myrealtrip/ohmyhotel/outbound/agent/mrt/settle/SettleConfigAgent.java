package com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle;


import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.Mrt30ResponseHandler;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception.MrtAgent4xxException;
import com.myrealtrip.settle.web.values.SettlementConfigResponse;
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
public class SettleConfigAgent {

    public static final String URI = "/api/v1/internal/settlement-configs/{partnerId}";
    public static final String SETTLE_CONFIG = "Settle Config";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final Mrt30ResponseHandler mrt30ResponseHandler;

    public SettleConfigAgent(@Qualifier("settleConfigWebClient") WebClient settleConfigWebClient,
                             CircuitBreakerFactory circuitBreakerFactory,
                             Mrt30ResponseHandler mrt30ResponseHandler) {
        this.webClient = settleConfigWebClient;
        this.circuitBreaker = circuitBreakerFactory.create(SETTLE_CONFIG);
        this.mrt30ResponseHandler = mrt30ResponseHandler;
    }

    public SettlementConfigResponse getSettlementConfig(String partnerId) {
        return getSettlementConfigMono(partnerId).block();
    }

    public Mono<SettlementConfigResponse> getSettlementConfigMono(String partnerId) {
        return webClient.get()
            .uri(URI, partnerId)
            .exchangeToMono(res -> mrt30ResponseHandler.decode(res, SettlementConfigResponse.class))
            .onErrorMap(mrt30ResponseHandler::resolveError)
            .transform(CircuitBreakerOperator.of(this.circuitBreaker))
            .retryWhen(Retry.backoff(1, Duration.ofMillis(500))
                           .filter(throwable -> !(throwable instanceof MrtAgent4xxException))
                           .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()));
    }
}
