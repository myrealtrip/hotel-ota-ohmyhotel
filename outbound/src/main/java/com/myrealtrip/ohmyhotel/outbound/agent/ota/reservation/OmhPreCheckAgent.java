package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentConstants;
import com.myrealtrip.ohmyhotel.outbound.agent.common.CircuitBreakerFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.OmhAgentSupport;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
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
public class OmhPreCheckAgent {

    public static final String URI = "/channel/ota/v2.0/reservation/precheck";
    private static final String PRE_CHECK = "Pre Check";

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final OmhAgentSupport omhAgentSupport;

    public OmhPreCheckAgent(WebClient omhPreCheckWebClient,
                                      CircuitBreakerFactory circuitBreakerFactory,
                                      OmhAgentSupport omhAgentSupport) {
        this.webClient = omhPreCheckWebClient;
        this.omhAgentSupport = omhAgentSupport;
        this.circuitBreaker = circuitBreakerFactory.create(PRE_CHECK);
    }

    public OmhPreCheckResponse preCheck(OmhPreCheckRequest request) {
        try {
            return preCheckMono(request).block();
        } catch (OmhApiException e) {
            log.error(AgentConstants.LOG_FORMAT, PRE_CHECK, ObjectMapperUtils.writeAsString(request), e.getResponse());
            throw e;
        } catch (Throwable e) {
            log.error(AgentConstants.LOG_FORMAT, PRE_CHECK, ObjectMapperUtils.writeAsString(request), "");
            throw e;
        }
    }

    public Mono<OmhPreCheckResponse> preCheckMono(OmhPreCheckRequest request) {
        return webClient.post()
            .uri(URI)
            .headers(omhAgentSupport::setAuthHeader)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatus::isError, res -> omhAgentSupport.getOmhApiExceptionMono(PRE_CHECK, res))
            .bodyToMono(OmhPreCheckResponse.class)
            .map(res -> omhAgentSupport.checkFail(res, PRE_CHECK))
            .transform(CircuitBreakerOperator.of(this.circuitBreaker));
    }
}
