package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCancelBookingResponse;
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
public class OmhCancelBookingAgent {

    public static final String URI = "/channel/ota/v2.0/reservation/book/{channelBookingCode}/cancel";
    private static final String CANCEL_BOOKING = "Cancel Booking";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhCancelBookingAgent(WebClient omhCancelBookingWebClient,
                                 CircuitBreakerFactory circuitBreakerFactory,
                                 OmhAgentSupport omhAgentSupport) {
        this.webClient = omhCancelBookingWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(CANCEL_BOOKING);
    }

    public OmhCancelBookingResponse cancelBooking(String mrtReservationNo) {
        String request = "channelBookingCode: " + mrtReservationNo;
        try {
            return cancelBookingMono(mrtReservationNo).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, CANCEL_BOOKING, request, e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, CANCEL_BOOKING, request, "");
            throw e;
        }
    }

    public Mono<OmhCancelBookingResponse> cancelBookingMono(String mrtReservationNo) {
        return webClient.get()
            .uri(URI, mrtReservationNo)
            .headers(omhAgentSupport::setAuthHeader)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(CANCEL_BOOKING, res))
            .bodyToMono(OmhCancelBookingResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, CANCEL_BOOKING))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
