package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomInfoResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
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
public class OmhRoomInfoAgent {

    private static final String URI = "/channel/ota/v2.0/hotels/rooms/information";
    private static final String ROOM_INFO = "Room Info";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhRoomInfoAgent(WebClient omhRoomInfoWebClient,
                                      CircuitBreakerFactory circuitBreakerFactory,
                                      OmhAgentSupport omhAgentSupport) {
        this.webClient = omhRoomInfoWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(ROOM_INFO);
    }

    public OmhRoomInfoResponse getRoomInfo(OmhRoomInfoRequest request) {
        try {
            return getRomInfoMono(request).block();
        } catch(OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, ROOM_INFO, ObjectMapperUtils.writeAsString(request), ObjectMapperUtils.writeAsString(e.getOmhCommonResponse()));
            throw e;
        } catch(Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, ROOM_INFO, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhRoomInfoResponse> getRomInfoMono(OmhRoomInfoRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(ROOM_INFO, res))
            .bodyToMono(OmhRoomInfoResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, ROOM_INFO))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
