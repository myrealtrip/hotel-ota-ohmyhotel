package com.myrealtrip.ohmyhotel.outbound.agent.ota.exception;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class OmhApiException extends RuntimeException {

    private static final String MESSAGE = "OhMyHotel [%s] API %s Error: %s";

    private final String response;

    private final int httpStatusCode;

    public <T extends OmhCommonResponse> OmhApiException(String apiName, T response, int httpStatusCode) {
        super(String.format(MESSAGE, apiName, httpStatusCode, String.join(":-:", response.getAllErrorMessages())));
        this.httpStatusCode = httpStatusCode;
        this.response = ObjectMapperUtils.writeAsString(response);
    }
}
