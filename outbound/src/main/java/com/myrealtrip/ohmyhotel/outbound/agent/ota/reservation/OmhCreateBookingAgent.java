package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
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
public class OmhCreateBookingAgent {

    public static final String URI = "/channel/ota/v2.0/reservation/book";
    private static final String CREATE_BOOKING = "Create Booking";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhCreateBookingAgent(WebClient omhCreateBookingWebClient,
                            CircuitBreakerFactory circuitBreakerFactory,
                            OmhAgentSupport omhAgentSupport) {
        this.webClient = omhCreateBookingWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CREATE_BOOKING);
    }

    public OmhCreateBookingResponse crateBooking(OmhCreateBookingRequest request) {
        try {
            return createBookingMono(request).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, CREATE_BOOKING, ObjectMapperUtils.writeAsString(request), e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, CREATE_BOOKING, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhCreateBookingResponse> createBookingMono(OmhCreateBookingRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(CREATE_BOOKING, res))
            .bodyToMono(OmhCreateBookingResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, CREATE_BOOKING))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
