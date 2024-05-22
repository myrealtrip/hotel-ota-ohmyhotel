package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.PenaltyBasis;
import com.myrealtrip.ohmyhotel.enumarate.RateOrAmount;
import com.myrealtrip.ohmyhotel.utils.DateTimeUtils;
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

        private String fromDateTime;

        private String toDateTime;

        private RateOrAmount rateOrAmount;

        private BigDecimal penaltyValue;

        @JsonIgnore
        public LocalDateTime getLocalDateTimeOfFromDateTime() {
            try {
                return LocalDateTime.parse(fromDateTime, DateTimeUtils.OMH_CANCEL_POLICY_DATE_TIME_FORMAT1);
            } catch (Exception e) {
                return LocalDateTime.parse(fromDateTime, DateTimeUtils.OMH_CANCEL_POLICY_DATE_TIME_FORMAT2);
            }
        }

        @JsonIgnore
        public LocalDateTime getLocalDateTimeOfToDateTime() {
            try {
                return LocalDateTime.parse(toDateTime, DateTimeUtils.OMH_CANCEL_POLICY_DATE_TIME_FORMAT1);
            } catch (Exception e) {
                return LocalDateTime.parse(toDateTime, DateTimeUtils.OMH_CANCEL_POLICY_DATE_TIME_FORMAT2);
            }
        }
    }
}
