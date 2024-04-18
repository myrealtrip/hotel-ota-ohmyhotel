package com.myrealtrip.ohmyhotel.outbound.agent.mrt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myrealtrip.common.values.StatusResult;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception.MrtAgent4xxException;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception.MrtAgent5xxException;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception.MrtAgentException;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.protocol.AgentError;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.protocol.AgentResource;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.util.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
public class Mrt30ResponseHandler {

    private final String name;
    private final ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();

    @Autowired
    public Mrt30ResponseHandler() {
        this.name = "mrt internal";
    }

    public Mrt30ResponseHandler(String name) {
        this.name = name;
    }

    public <T> Function<ClientResponse, Mono<List<T>>> decodeList(Class<T> clazz) {
        return res -> decodeList(res, clazz);
    }

    public <T> Mono<List<T>> decodeList(ClientResponse res, Class<T> clazz) {

        if (res.statusCode().is3xxRedirection() || res.statusCode().is1xxInformational()) {
            return Mono.error(getBaseException("not parsed", res.statusCode().value()));
        }

        return res.bodyToMono(AgentResource.class).map(e -> {
            if (e.isOk()) { // status 200
                if (Objects.isNull(e.getData())) {
                    return List.of();
                }
                return objectMapper.convertValue(e.getData(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } else { // status not 200 or http not 200. need to log at least. not really concerns about where to take care of Exception or its format.
                handleError(e);
                return null;
            }
        });
    }

    public <T> Mono<T> decode(ClientResponse res, Class<T> clazz) {

        if (res.statusCode().is3xxRedirection() || res.statusCode().is1xxInformational()) {
            return Mono.error(getBaseException("not parsed", res.statusCode().value()));
        }

        return res.bodyToMono(AgentResource.class).map(e -> {
            AgentResource<T> resource = objectMapper.convertValue(e, objectMapper.getTypeFactory().constructParametricType(AgentResource.class, clazz));
            if (e.isOk()) { // status 200
                return resource.getData();
            } else { // status not 200 or http not 200. need to log at least. not really concerns about where to take care of Exception or its format.
                handleError(resource);
                return null;
            }
        });

    }

    /**
     * all the errors have to be wrapped since there is a chance error is not related to HTTP
     */
    public Throwable resolveError(Throwable err) {
        if (!MrtAgentException.class.isAssignableFrom(err.getClass())) {
            return getBaseException("web client related error on " + name + " agent", err);
        }
        return err;
    }

    private String makeErrorTrace(AgentError error) {
        if (Objects.nonNull(error)) {
            return String.join("\n", error.getTraces());
        } else {
            return StringUtils.EMPTY;
        }
    }

    private void handleError(AgentResource<?> resource) {
        log.error("Remote message : {}", resource.getResult().getMessage());
        if (Objects.nonNull(resource.getData())) {
            AgentError error = objectMapper.convertValue(resource.getData(), AgentError.class);
            log.error("Remote Exception is {}", makeErrorTrace(error));
        }

        throwByResourceStatusResult(resource.getResult());
    }

    public void throwByResourceStatusResult(StatusResult statusResult) {
        if (AgentUtils.is4xxStatus(statusResult.getStatus())) {
            throw  get4xxException("4xx error on " + name + " agent", statusResult.getStatus(), statusResult.getCode());
        } else if (AgentUtils.is5xxStatus(statusResult.getStatus())) {
            throw get5xxException("5xx error on " + name + " agent", statusResult.getStatus(), statusResult.getCode());
        }
        throw getBaseException("http related error on " + name + " agent", statusResult.getStatus());
    }

    protected MrtAgentException get4xxException(String message, int status, String code) {
        throw  new MrtAgent4xxException(message, status, code);
    }

    protected MrtAgentException get5xxException(String message, int status, String code) {
        throw new MrtAgent5xxException(message, status, code);
    }

    protected MrtAgentException getBaseException(String message, int status) {
        throw new MrtAgentException(message, status);
    }

    protected MrtAgentException getBaseException(String message, Throwable err) {
        throw new MrtAgentException(message, err, 500);
    }
}
