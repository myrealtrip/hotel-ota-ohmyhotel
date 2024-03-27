package com.myrealtrip.ohmyhotel.outbound.agent.ota.exception;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@Getter
public class OmhApiException extends RuntimeException {

    private static final String MESSAGE = "[%s] Ohmyhotel %s API Error: %s";

    private final OmhCommonResponse omhCommonResponse;

    private final int httpStatusCode;

    public OmhApiException(String apiName, OmhCommonResponse omhCommonResponse, int httpStatusCode) {
        super(String.format(MESSAGE, apiName, httpStatusCode, String.join(":-:", omhCommonResponse.getAllErrorMessages())));
        this.httpStatusCode = httpStatusCode;
        this.omhCommonResponse = omhCommonResponse;
    }
}
