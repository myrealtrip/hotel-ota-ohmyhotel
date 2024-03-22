package com.myrealtrip.ohmyhotel.outbound.agent.ota.exception;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OmhApiException extends RuntimeException {

    private static final String MESSAGE = "Ohmyhotel %s API Error: %s";

    private final OmhCommonResponse omhCommonResponse;

    public OmhApiException(String uri, OmhCommonResponse omhCommonResponse) {
        super(String.format(MESSAGE, uri, omhCommonResponse.getErrorMessage()));
        log.error("{}", ObjectMapperUtils.writeAsString(omhCommonResponse));
        this.omhCommonResponse = omhCommonResponse;
    }
}
