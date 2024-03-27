package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OmhCreateBookingAgent {

    private static final String CIRCUIT_BREAKER_NAME = "OmhCreateBookingAgent";
    private static final String URI = "/channel/ota/v2.0/reservation/book";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhCreateBookingAgent(WebClient omhCreateBookingWebClient,
                            CircuitBreakerFactory circuitBreakerFactory,
                            OmhAgentSupport omhAgentSupport) {
        this.webClient = omhCreateBookingWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
    }

    public OmhCreateBookingResponse crateBooking(OmhCreateBookingRequest request) {
        return createBookingMono(request).block();
    }

    public Mono<OmhCreateBookingResponse> createBookingMono(OmhCreateBookingRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(URI, res))
            .bodyToMono(OmhCreateBookingResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, URI))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
