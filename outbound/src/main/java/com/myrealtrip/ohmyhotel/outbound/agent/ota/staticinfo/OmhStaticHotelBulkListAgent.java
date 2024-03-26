package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class OmhStaticHotelBulkListAgent {

    private static final String CIRCUIT_BREAKER_NAME = "OmhStaticHotelBulkListAgent";
    private static final String URI = "/channel/ota/v2.0/static/hotels/{lastUpdatedAt}/{hotelCode}";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhStaticHotelBulkListAgent(WebClient omhStaticHotelBulkListWebClient,
                               CircuitBreakerFactory circuitBreakerFactory,
                               OmhAgentSupport omhAgentSupport) {
        this.webClient = omhStaticHotelBulkListWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
    }

    public OmhStaticBulkHotelListResponse getBulkHotels(LocalDate lastUpdatedDate, Long hotelCode) {
        return getBulkHotelsMono(lastUpdatedDate, hotelCode).block();
    }

    public Mono<OmhStaticBulkHotelListResponse> getBulkHotelsMono(LocalDate lastUpdatedDate, Long hotelCode) {
        return webClient.get()
            .uri(URI, lastUpdatedDate, hotelCode)
            .headers(omhAgentSupport::setAuthHeader)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(URI, res))
            .bodyToMono(OmhStaticBulkHotelListResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, URI))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
