package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OmhHotelsAvailabilityAgent {

    private static final String CIRCUIT_BREAKER_NAME = "OmhHotelsAvailabilityAgent";
    private static final String URI = "/channel/ota/v2.0/hotels/availability";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhHotelsAvailabilityAgent(WebClient omhHotelsAvailabilityWebClient,
                                       CircuitBreakerFactory circuitBreakerFactory,
                                       OmhAgentSupport omhAgentSupport) {
        this.webClient = omhHotelsAvailabilityWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
    }

    public OmhHotelsAvailabilityResponse getAvailability(OmhHotelsAvailabilityRequest request) {
        return getAvailabilityMono(request).block();
    }

    public Mono<OmhHotelsAvailabilityResponse> getAvailabilityMono(OmhHotelsAvailabilityRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(URI, res))
            .bodyToMono(OmhHotelsAvailabilityResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, URI))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
