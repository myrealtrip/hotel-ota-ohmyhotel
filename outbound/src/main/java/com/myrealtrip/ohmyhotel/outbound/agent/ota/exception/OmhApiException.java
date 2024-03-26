package com.myrealtrip.ohmyhotel.outbound.agent.ota.exception;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class OmhApiException extends RuntimeException {

    private static final String MESSAGE = "[%s] Ohmyhotel %s API Error: %s";

    private final OmhCommonResponse omhCommonResponse;

    private final int httpStatusCode;

    public OmhApiException(String uri, OmhCommonResponse omhCommonResponse, int httpStatusCode) {
        super(String.format(MESSAGE, uri, httpStatusCode, String.join(":-:", omhCommonResponse.getAllErrorMessages())));
        log.error("{}", ObjectMapperUtils.writeAsString(omhCommonResponse));
        this.httpStatusCode = httpStatusCode;
        this.omhCommonResponse = omhCommonResponse;
    }
}
