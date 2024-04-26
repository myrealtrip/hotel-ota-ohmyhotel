package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
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
public class OmhHotelsAvailabilityAgent {

    private static final String URI = "/channel/ota/v2.0/hotels/availability";
    private static final String HOTELS_AVAILABILITY = "Hotels Availability";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhHotelsAvailabilityAgent(WebClient omhHotelsAvailabilityWebClient,
                                      CircuitBreakerFactory circuitBreakerFactory,
                                      OmhAgentSupport omhAgentSupport) {
        this.webClient = omhHotelsAvailabilityWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(HOTELS_AVAILABILITY);
    }

    public OmhHotelsAvailabilityResponse getHotelsAvailability(OmhHotelsAvailabilityRequest request) {
        try {
            return getHotelsAvailabilityMono(request).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, HOTELS_AVAILABILITY, ObjectMapperUtils.writeAsString(request), e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, HOTELS_AVAILABILITY, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhHotelsAvailabilityResponse> getHotelsAvailabilityMono(OmhHotelsAvailabilityRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(HOTELS_AVAILABILITY, res))
            .bodyToMono(OmhHotelsAvailabilityResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, HOTELS_AVAILABILITY))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
