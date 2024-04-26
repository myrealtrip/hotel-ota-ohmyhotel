package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
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
public class OmhRoomsAvailabilityAgent {

    public static final String URI = "/channel/ota/v2.0/hotels/rooms/availability";
    private static final String ROOMS_AVAILABILITY = "Rooms Availability";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhRoomsAvailabilityAgent(WebClient omhRoomsAvailabilityWebClient,
                                      CircuitBreakerFactory circuitBreakerFactory,
                                      OmhAgentSupport omhAgentSupport) {
        this.webClient = omhRoomsAvailabilityWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(ROOMS_AVAILABILITY);
    }

    public OmhRoomsAvailabilityResponse getRoomsAvailability(OmhRoomsAvailabilityRequest request) {
        try {
            return getRoomsAvailabilityMono(request).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, ROOMS_AVAILABILITY, ObjectMapperUtils.writeAsString(request), e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, ROOMS_AVAILABILITY, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhRoomsAvailabilityResponse> getRoomsAvailabilityMono(OmhRoomsAvailabilityRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(ROOMS_AVAILABILITY, res))
            .bodyToMono(OmhRoomsAvailabilityResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, ROOMS_AVAILABILITY))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
