package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhCreateBookingResponse extends OmhCommonResponse {

    private String channelBookingCode;

    private String ohMyBookingCode;
}
