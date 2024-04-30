package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.PenaltyBasis;
import com.myrealtrip.ohmyhotel.enumarate.RateOrAmount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhCancelPolicy {

    private Boolean isNonRefundable;

    private String timeZone;

    private PenaltyBasis penaltyBasis;

    private List<OmhCancelPolicyValue> policies;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhCancelPolicyValue {

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime fromDateTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime toDateTime;

        private RateOrAmount rateOrAmount;

        private BigDecimal penaltyValue;
    }
}
