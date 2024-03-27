package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhCancelBookingResponse extends OmhCommonResponse {

    private String cancelConfirmNo;

    private String currencyCode;

    private BigDecimal cancelPenaltyAmount;
}
