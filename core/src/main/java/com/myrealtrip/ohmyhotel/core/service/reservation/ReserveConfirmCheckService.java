package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveConfirmCheckService {

    private final ReservationProvider reservationProvider;
    private final OmhBookingDetailAgent omhBookingDetailAgent;
    private final ReservationApiLogService reservationApiLogService;
    private final ReservationSlackSender reservationSlackSender;

    /**
     * 오마이호텔 예약상세 API 를 호출하여 예약 확정이 되었는지 상태값을 확인하고 DB 에 업데이트한다.
     */
    @Transactional
    public void checkOmhBookDetailAndUpdateReservation(Reservation reservation) {
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, StringUtils.EMPTY);
        OmhBookingDetailResponse omhBookingDetailResponse;
        try {
            omhBookingDetailResponse = omhBookingDetailAgent.bookingDetail(reservation.getMrtReservationNo());
        } catch (OmhApiException omhApiException) {
            saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, omhApiException.getResponse());
            handleBookDetailFail(reservation, omhApiException);
            return;
        } catch (Throwable t) {
            handleBookDetailFail(reservation, t);
            return;
        }
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhBookingDetailResponse));
        updateReservationByOmhBookingDetailResponse(reservation, omhBookingDetailResponse);
    }

    private void updateReservationByOmhBookingDetailResponse(Reservation reservation, OmhBookingDetailResponse omhBookingDetailResponse) {
        String omhBookCode = omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode();
        String hotelConfirmNo = omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo();
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.CONFIRMED) {
            reservationProvider.confirm(reservation.getReservationId(), omhBookCode, hotelConfirmNo);
            return;
        }
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.PENDING) {
            reservationProvider.confirmPending(reservation.getReservationId(), omhBookCode, hotelConfirmNo);
            reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_PENDING, reservation.getMrtReservationNo(), null);
            return;
        }
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.UNAVAILABLE ||
            omhBookingDetailResponse.getStatus() == OmhBookingStatus.CANCELLED) {
            handleConfirmFail(reservation);
        }
    }

    private void handleConfirmFail(Reservation reservation) {
        log.error("{} - " + ReservationSlackEvent.RESERVE_CONFIRM_FAIL.getNote(), reservation.getMrtReservationNo());
        reservationProvider.confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
        reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, reservation.getMrtReservationNo(), null);
    }

    private void handleBookDetailFail(Reservation reservation, Throwable t) {
        log.error("{} - " + ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL.getNote(), reservation.getMrtReservationNo(), t);
        reservationProvider.confirmPending(reservation.getReservationId(), null, null);
        reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, reservation.getMrtReservationNo(), null);
    }

    private void saveBookingDetailApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveBookingDetailForConfirmCheckLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} - booking detail api log save fail", mrtReservationNo);
        }
    }
}
