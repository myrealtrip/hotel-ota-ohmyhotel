package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.OmhPreCheckStatus;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhRoomOccupancy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhPreCheckResponse extends OmhCommonResponse {

    private OmhPreCheckStatus status;

    private OmhRoomOccupancy occupancy;

    private OmhPreCheckAmount amount;

    private OmhCancelPolicy cancellationPolicy;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhPreCheckAmount {

        private String currency;

        private BigDecimal totalNetAmount;

        private BigDecimal totalMspAmount;

        private RateType rateType;

        private List<OmhNightlyAmount> nightly;
    }
}
