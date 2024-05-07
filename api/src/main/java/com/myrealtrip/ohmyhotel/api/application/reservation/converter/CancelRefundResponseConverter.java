package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingStatusConverter;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.srtcommon.support.currency.CurrencyCode;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.dto.hotelota.booking.response.ItineraryCancelRefundResponse;
import com.myrealtrip.unionstay.dto.hotelota.booking.response.ItineraryRoomCancelRefund;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CancelRefundResponseConverter {

    private final BookingStatusConverter bookingStatusConverter;

    public ItineraryCancelRefundResponse toCancelRefundResponse(Reservation reservation, BigDecimal cancelPenaltyDepositPrice) {
        BigDecimal cancelPenaltySalePrice = OmhPriceCalculateUtils.toSalePrice(cancelPenaltyDepositPrice, reservation.getMrtCommissionRate());
        BigDecimal refundPrice = reservation.getSalePrice().subtract(cancelPenaltySalePrice);
        BigDecimal mrtCancelCommission = cancelPenaltySalePrice.subtract(cancelPenaltyDepositPrice);

        return ItineraryCancelRefundResponse.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .bookingStatus(bookingStatusConverter.toBookingStatus(reservation.getReservationStatus()))
            .providerBookingId(String.valueOf(reservation.getReservationId()))
            .mrtReservationNo(reservation.getMrtReservationNo())
            .amount(refundPrice)
            .totalCancelCommissionAmount(cancelPenaltySalePrice)
            .mrtCancelCommissionAmount(mrtCancelCommission)
            .roomCancelRefunds(List.of(ItineraryRoomCancelRefund.builder()
                                           .amount(refundPrice)
                                           .totalCancelCommissionAmount(cancelPenaltySalePrice)
                                           .mrtCancelCommissionAmount(mrtCancelCommission)
                                           .currency(CurrencyCode.KRW)
                                           .build()))
            .providerCode(ProviderCode.STAYNET)
            .providerType(ProviderType.MRT)
            .bookingErrorCode(null)
            .build();
    }
}
