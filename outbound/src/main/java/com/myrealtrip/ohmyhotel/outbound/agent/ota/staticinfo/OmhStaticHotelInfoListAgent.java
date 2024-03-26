package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OmhStaticHotelInfoListAgent {

    private static final String CIRCUIT_BREAKER_NAME = "OmhStaticHotelInfoListAgent";
    private static final String URI = "/channel/ota/v2.0/static/information";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhStaticHotelInfoListAgent(WebClient omhStaticHotelInfoListWebClient,
                                       CircuitBreakerFactory circuitBreakerFactory,
                                       OmhAgentSupport omhAgentSupport) {
        this.webClient = omhStaticHotelInfoListWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
    }

    public OmhStaticHotelInfoListResponse getHotelInfo(OmhStaticHotelInfoListRequest request) {
        return getHotelInfoMono(request).block();
    }

    public Mono<OmhStaticHotelInfoListResponse> getHotelInfoMono(OmhStaticHotelInfoListRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OmhStaticHotelInfoListResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, URI))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
