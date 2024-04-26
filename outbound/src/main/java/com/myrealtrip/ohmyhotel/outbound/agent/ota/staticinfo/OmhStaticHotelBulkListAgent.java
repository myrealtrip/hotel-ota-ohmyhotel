package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@Slf4j
public class OmhStaticHotelBulkListAgent {

    private static final String URI = "/channel/ota/v2.0/static/hotels/{lastUpdatedAt}/{hotelCode}";
    private static final String STATIC_HOTEL_BULK_LIST = "Static Hotel Bulk List";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhStaticHotelBulkListAgent(WebClient omhStaticHotelBulkListWebClient,
                               CircuitBreakerFactory circuitBreakerFactory,
                               OmhAgentSupport omhAgentSupport) {
        this.webClient = omhStaticHotelBulkListWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(STATIC_HOTEL_BULK_LIST);
    }

    public OmhStaticBulkHotelListResponse getBulkHotels(LocalDate lastUpdatedDate, Long hotelCode) {
        String requestFormat = "lastUpdateDate: %s, hotelCode: %s";
        try {
            return getBulkHotelsMono(lastUpdatedDate, hotelCode).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, STATIC_HOTEL_BULK_LIST, String.format(requestFormat, lastUpdatedDate, hotelCode), e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, STATIC_HOTEL_BULK_LIST, String.format(requestFormat, lastUpdatedDate, hotelCode), "");
            throw e;
        }
    }

    public Mono<OmhStaticBulkHotelListResponse> getBulkHotelsMono(LocalDate lastUpdatedDate, Long hotelCode) {
        return webClient.get()
            .uri(URI, lastUpdatedDate, hotelCode)
            .headers(omhAgentSupport::setAuthHeader)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(STATIC_HOTEL_BULK_LIST, res))
            .bodyToMono(OmhStaticBulkHotelListResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, STATIC_HOTEL_BULK_LIST))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
