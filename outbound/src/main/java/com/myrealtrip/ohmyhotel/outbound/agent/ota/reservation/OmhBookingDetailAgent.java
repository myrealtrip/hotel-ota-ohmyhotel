package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
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
public class OmhBookingDetailAgent {

    private static final String URI = "/channel/ota/v2.0/reservation/book/{channelBookingCode}";
    private static final String BOOKING_DETAIL = "Booking-Detail";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhBookingDetailAgent(WebClient omhBookingDetailWebClient,
                                 CircuitBreakerFactory circuitBreakerFactory,
                                 OmhAgentSupport omhAgentSupport) {
        this.webClient = omhBookingDetailWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(BOOKING_DETAIL);
    }

    public OmhBookingDetailResponse bookingDetail(String mrtReservationNo) {
        try {
            return bookingDetailMono(mrtReservationNo).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, BOOKING_DETAIL, mrtReservationNo, ObjectMapperUtils.writeAsString(e.getOmhCommonResponse()));
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, BOOKING_DETAIL, mrtReservationNo, "");
            throw e;
        }
    }

    public Mono<OmhBookingDetailResponse> bookingDetailMono(String mrtReservationNo) {
        return webClient.get()
            .uri(URI, mrtReservationNo)
            .headers(omhAgentSupport::setAuthHeader)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(BOOKING_DETAIL, res))
            .bodyToMono(OmhBookingDetailResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, BOOKING_DETAIL))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
