package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OmhStaticHotelInfoListAgent {

    private static final String URI = "/channel/ota/v2.0/static/information";
    private static final String STATIC_HOTEL_INFO = "Static Hotel Information";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhStaticHotelInfoListAgent(WebClient omhStaticHotelInfoListWebClient,
                                       CircuitBreakerFactory circuitBreakerFactory,
                                       OmhAgentSupport omhAgentSupport) {
        this.webClient = omhStaticHotelInfoListWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(STATIC_HOTEL_INFO);
    }

    public OmhStaticHotelInfoListResponse getHotelInfo(OmhStaticHotelInfoListRequest request) {
        try {
            return getHotelInfoMono(request).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, STATIC_HOTEL_INFO, ObjectMapperUtils.writeAsString(request), ObjectMapperUtils.writeAsString(e.getOmhCommonResponse()));
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, STATIC_HOTEL_INFO, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhStaticHotelInfoListResponse> getHotelInfoMono(OmhStaticHotelInfoListRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(STATIC_HOTEL_INFO, res))
            .bodyToMono(OmhStaticHotelInfoListResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, STATIC_HOTEL_INFO))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
