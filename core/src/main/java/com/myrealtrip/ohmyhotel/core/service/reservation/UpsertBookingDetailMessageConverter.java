package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.service.BedDescriptionConverter;
import com.myrealtrip.ohmyhotel.core.service.CommonSearchResponseConverter;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.srtcommon.support.currency.CurrencyCode;
import com.myrealtrip.unionstay.common.constant.AmountType;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import com.myrealtrip.unionstay.common.constant.booking.BookingStatus;
import com.myrealtrip.unionstay.common.constant.booking.RoomBookingStatus;
import com.myrealtrip.unionstay.common.message.booking.UpsertBookingDetailMessage;
import com.myrealtrip.unionstay.common.model.booking.retrieve.ItineraryRoom;
import com.myrealtrip.unionstay.common.model.booking.retrieve.ReservationProfits;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomBookingAmount;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomBookingBed;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomBookingDailyAmount;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomBookingRateBenefit;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomBookingTotalPayment;
import com.myrealtrip.unionstay.common.model.booking.retrieve.RoomCancelReason;
import com.myrealtrip.unionstay.common.model.booking.retrieve.Voucher;
import com.myrealtrip.unionstay.common.model.booking.retrieve.VoucherReservationNo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpsertBookingDetailMessageConverter {

    private static final String OTA_RESERVATION_NO_NAME_LEVEL_1 = "연동사 예약번호";
    private static final String SUPPLIER_RESERVATION_NO_NAME_LEVEL_1 = "호텔 예약번호";

    private final BedDescriptionConverter bedDescriptionConverter;
    private final CommonSearchResponseConverter commonSearchResponseConverter;
    private final BookingStatusConverter bookingStatusConverter;

    public UpsertBookingDetailMessage toMessage(Reservation reservation, int retryCount) {
        return UpsertBookingDetailMessage.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .providerBookingId(String.valueOf(reservation.getReservationId()))
            .mrtReservationNo(reservation.getMrtReservationNo())
            .bookingStatus(bookingStatusConverter.toBookingStatus(reservation.getReservationStatus()))
            .bookingErrorCode(EnumUtils.getEnum(BookingErrorCode.class, reservation.getBookingErrorCode()))
            .retryCount(retryCount)
            .rooms(List.of(toRoom(reservation)))
            .updatedDateTime(LocalDateTime.now())
            .build();
    }

    public ItineraryRoom toRoom(Reservation reservation) {
        return ItineraryRoom.builder()
            .providerRoomBookingId(String.valueOf(reservation.getReservationId()))
            .providerPropertyId(String.valueOf(reservation.getHotelId()))
            .providerRoomId(reservation.getRoomTypeCode())
            .providerRateId(reservation.getRatePlanCode())
            .roomBookingStatus(bookingStatusConverter.toRoomBookingStatus(reservation.getReservationStatus()))
            .bookingErrorCode(EnumUtils.getEnum(BookingErrorCode.class, reservation.getBookingErrorCode()))
            .checkin(reservation.getCheckInDate())
            .checkout(reservation.getCheckOutDate())
            .propertyName(reservation.getHotelName())
            .roomName(reservation.getRoomTypeName())
            .rateName(reservation.getRatePlanName())
            .bed(toBed(reservation))
            .benefits(toBenefits(reservation))
            .onPromotion(false)
            .cancelPenalties(commonSearchResponseConverter.toCancelPolicies(reservation.getAdditionalInfo().getCancelPolicy(), reservation.getMrtCommissionRate()))
            .totalPayment(toTotalPayment(reservation))
            .profits(toProfits(reservation))
            .dailyAmountList(toDailyAmountList(reservation))
            .surcharges(Collections.emptyList())
            .onSiteSurcharges(List.of())
            .monetaryPromotion(null)
            .nonMonetaryPromotions(List.of())
            .cancelReason(toRoomCancelReason(reservation))
            .voucher(toVoucher(reservation))
            .monetaryPromotions(Collections.emptyList())
            .build();
    }

    private RoomBookingBed toBed(Reservation reservation) {
        return RoomBookingBed.builder()
            .id(null)
            .description(bedDescriptionConverter.toUnionStayBedDescription(reservation.getAdditionalInfo().getBedGroups()))
            .build();
    }

    private List<RoomBookingRateBenefit> toBenefits(Reservation reservation) {
        MealBasisCode mealBasisCode = reservation.getAdditionalInfo().getMealBasisCode();
        if (isNull(mealBasisCode) ||
            StringUtils.isBlank(mealBasisCode.getExposedName())) {
            return Collections.emptyList();
        }
        RoomBookingRateBenefit rateBenefit = RoomBookingRateBenefit.builder()
            .id(mealBasisCode.name())
            .benefitName(mealBasisCode.getExposedName())
            .build();
        return List.of(rateBenefit);
    }

    // 통합숙소는 multi-room 예약이 불가능하니 reservation 의 가격정보로 세팅
    private RoomBookingTotalPayment toTotalPayment(Reservation reservation) {
        return RoomBookingTotalPayment.builder()
            .currency(CurrencyCode.KRW)
            .inclusive(BooleanUtils.isTrue(reservation.getZeroMarginApply()) ?
                       reservation.getZeroMarginApplyPrice() :
                       reservation.getSalePrice())
            .exclusive(BooleanUtils.isTrue(reservation.getZeroMarginApply()) ?
                       reservation.getZeroMarginApplyPrice() :
                       reservation.getSalePrice())
            .taxAndServiceFee(BigDecimal.valueOf(0))
            .refundedAmount(reservation.getCancelRefundAmount())
            .build();
    }

    private ReservationProfits toProfits(Reservation reservation) {
        return ReservationProfits.builder()
            .reservationProfit(reservation.getMrtCommission())
            .cancellationProfit(reservation.getMrtCancelCommission())
            .build();
    }

    // 통합숙소는 multi-room 예약이 불가능하니 reservation 의 가격정보로 세팅
    private List<RoomBookingDailyAmount> toDailyAmountList(Reservation reservation) {
        return reservation.getAdditionalInfo().getNightlyAmounts().get(0).getAmounts().stream()
            .map(omhAmount -> RoomBookingDailyAmount.builder()
                .date(omhAmount.getDate())
                .amountList(List.of(new RoomBookingAmount(AmountType.BASE_RATE, OmhPriceCalculateUtils.toSalePrice(omhAmount.getNetAmount(), reservation.getMrtCommissionRate()))))
                .build())
            .collect(Collectors.toList());
    }

    private RoomCancelReason toRoomCancelReason(Reservation reservation) {
        return RoomCancelReason.builder()
            .cancelReasonType(reservation.getCancelReasonType())
            .reason(reservation.getCancelReason())
            .cancelAcceptedAt(reservation.getCanceledAt())
            .build();
    }

    private Voucher toVoucher(Reservation reservation) {
        return Voucher.builder()
            .otaReservationNos(toOtaReservationNos(reservation))
            .supplierReservationNos(toSupplierReservationNos(reservation))
            .build();
    }

    private List<VoucherReservationNo> toOtaReservationNos(Reservation reservation) {
        if (StringUtils.isBlank(reservation.getOmhBookCode())) {
            return List.of();
        }
        return List.of(
            VoucherReservationNo.builder()
                .level(0)
                .name(OTA_RESERVATION_NO_NAME_LEVEL_1)
                .value(reservation.getOmhBookCode())
                .build()
        );
    }

    private List<VoucherReservationNo> toSupplierReservationNos(Reservation reservation) {
        if (StringUtils.isBlank(reservation.getHotelConfirmNo())) {
            return List.of();
        }
        return List.of(
            VoucherReservationNo.builder()
                .level(0)
                .name(SUPPLIER_RESERVATION_NO_NAME_LEVEL_1)
                .value(reservation.getHotelConfirmNo())
                .build()
        );
    }
}
