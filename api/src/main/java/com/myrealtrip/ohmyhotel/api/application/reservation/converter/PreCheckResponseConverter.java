package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.enumarate.OmhPreCheckStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import com.myrealtrip.srtcommon.support.utils.NumericUtils;
import com.myrealtrip.unionstay.common.constant.booking.PreCheckStatus;
import com.myrealtrip.unionstay.dto.hotelota.precheck.response.PreCheckResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PreCheckResponseConverter {

    public PreCheckResponse toPreCheckResponse(OmhPreCheckResponse omhPreCheckResponse, Order order) {
        PreCheckStatus preCheckStatus = toPreCheckStatus(omhPreCheckResponse, order);
        return PreCheckResponse.builder()
            .reservable(preCheckStatus == PreCheckStatus.AVAILABLE)
            .status(preCheckStatus)
            .reason(StringUtils.EMPTY)
            .bookingApiKey(StringUtils.EMPTY)
            .build();
    }

    public PreCheckResponse toErrorPreCheckResponse() {
        return PreCheckResponse.builder()
            .reservable(false)
            .status(PreCheckStatus.INTERNAL_SERVER_ERROR)
            .reason(StringUtils.EMPTY)
            .bookingApiKey(StringUtils.EMPTY)
            .build();
    }

    public PreCheckStatus toPreCheckStatus(OmhPreCheckResponse omhPreCheckResponse, Order order) {
        if (omhPreCheckResponse.getStatus() == OmhPreCheckStatus.SOLD_OUT) {
            return PreCheckStatus.SOLD_OUT;
        } else if (!NumericUtils.equals(order.getDepositPrice(), omhPreCheckResponse.getAmount().getTotalNetAmount())) {
            return PreCheckStatus.PRICE_CHANGED;
        } else if (omhPreCheckResponse.getStatus() == OmhPreCheckStatus.AVAILABLE) {
            return PreCheckStatus.AVAILABLE;
        } else {
            throw new IllegalStateException("cannot mapping preCheck status");
        }
    }
}
