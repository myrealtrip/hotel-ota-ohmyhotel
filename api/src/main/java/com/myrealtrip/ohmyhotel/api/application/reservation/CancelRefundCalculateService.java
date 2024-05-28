package com.myrealtrip.ohmyhotel.api.application.reservation;

import com.myrealtrip.ohmyhotel.api.application.reservation.converter.CancelRefundResponseConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingCancelPolicyValue;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingDetailCancelPolicy;
import com.myrealtrip.ohmyhotel.utils.DateTimeUtils;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.booking.response.ItineraryCancelRefundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelRefundCalculateService {

    private static final Comparator<OmhBookingCancelPolicyValue> CANCEL_POLICY_COMPARATOR = Comparator.comparing(OmhBookingCancelPolicyValue::getLocalDateTimeOfFromDateTime);

    private final OmhBookingDetailAgent omhBookingDetailAgent;
    private final ReservationProvider reservationProvider;
    private final ReservationApiLogService reservationApiLogService;
    private final CancelRefundResponseConverter cancelRefundResponseConverter;

    public ItineraryCancelRefundResponse getCancelRefund(String mrtReservationNo) {
        OmhBookingDetailResponse omhBookingDetailResponse = omhBookingDetailAgent.bookingDetail(mrtReservationNo);
        Reservation reservation = reservationProvider.getByMrtReservationNoReadOnly(mrtReservationNo);
        BigDecimal cancelPenaltyDepositPrice = getCancelPenaltyDepositPrice(omhBookingDetailResponse.getCancellationPolicy(), reservation.getDepositPrice());
        saveApiLog(mrtReservationNo, omhBookingDetailResponse);
        return cancelRefundResponseConverter.toCancelRefundResponse(reservation, cancelPenaltyDepositPrice);
    }

    private BigDecimal getCancelPenaltyDepositPrice(OmhBookingDetailCancelPolicy omhBookingDetailCancelPolicy, BigDecimal depositPrice) {
        if (BooleanUtils.isTrue(omhBookingDetailCancelPolicy.getIsNonRefundable())) {
            return depositPrice; // 전액 패널티
        }
        LocalDateTime now = DateTimeUtils.now(ZoneId.of(omhBookingDetailCancelPolicy.getTimeZone()));
        List<OmhBookingCancelPolicyValue> sortedCancelPolicyDetails = omhBookingDetailCancelPolicy.getPolicies().stream()
            .sorted(CANCEL_POLICY_COMPARATOR)
            .collect(Collectors.toList());

        // 현재 날짜가 policy 날짜 안에 있을 경우
        for (OmhBookingCancelPolicyValue cancelPolicyDetail : sortedCancelPolicyDetails) {
            if (DateTimeUtils.goe(now, cancelPolicyDetail.getLocalDateTimeOfFromDateTime()) &&
                DateTimeUtils.loe(now, cancelPolicyDetail.getLocalDateTimeOfToDateTime())) {
                return cancelPolicyDetail.getPenaltyAmount();
            }
        }
        // 현재 날짜가 policy 날짜 밖에 있을 경우
        if (now.isBefore(sortedCancelPolicyDetails.get(0).getLocalDateTimeOfFromDateTime())) {
            return BigDecimal.ZERO;
        }
        return depositPrice;
    }

    private void saveApiLog(String mrtReservationNo, OmhBookingDetailResponse omhBookingDetailResponse) {
        reservationApiLogService.saveBookingDetailForRefundPriceLog(mrtReservationNo, ApiLogType.REQUEST, StringUtils.EMPTY);
        reservationApiLogService.saveBookingDetailForRefundPriceLog(mrtReservationNo, ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhBookingDetailResponse));
    }
}
