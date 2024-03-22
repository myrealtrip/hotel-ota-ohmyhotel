package com.myrealtrip.ohmyhotel.outbound.agent.ota.healthcheck;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.healthcheck.protocol.OmhHealthCheckResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OmhHealthCheckAgent {

    private static final String CIRCUIT_BREAKER_NAME = "OmhHealthCheckAgent";
    private static final String URI = "/channel/ota/v2.0/status";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhHealthCheckAgent(WebClient omhHealthCheckWebClient,
                               CircuitBreakerFactory circuitBreakerFactory,
                               OmhAgentSupport omhAgentSupport) {
        this.webClient = omhHealthCheckWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
    }

    public OmhHealthCheckResponse healthCheck() {
        return healthCheckMono().block();
    }

    public Mono<OmhHealthCheckResponse> healthCheckMono() {
        return webClient.get()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .retrieve()
            .bodyToMono(OmhHealthCheckResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, URI))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
