package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.consumer.reservation.converter.BookingOrderMessageConverter;
import com.myrealtrip.ohmyhotel.consumer.reservation.converter.OmhCreateBookingRequestConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCreateBookingAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveConfirmConsumeService {

    private final ReservationProvider reservationProvider;
    private final OmhBookingDetailAgent omhBookingDetailAgent;
    private final OmhCreateBookingAgent omhCreateBookingAgent;
    private final ReservationApiLogService reservationApiLogService;
    private final ReservationSlackSender reservationSlackSender;
    private final BookingOrderMessageConverter bookingOrderMessageConverter;
    private final OmhCreateBookingRequestConverter omhCreateBookingRequestConverter;

    /**
     * 예약 확정 메세지를 컨슘한다.
     * https://myrealtrip.atlassian.net/wiki/spaces/STP/pages/3719201097
     *
     * @param message
     */
    @Transactional
    public void consumeReserveConfirm(BookingOrderMessage message) {
        Reservation reservation = reservationProvider.getByMrtReservationNo(message.getMrtReservationNo());
        if (!reservation.getReservationStatus().canChangeTo(ReservationStatus.RESERVE_CONFIRM)) {
            log.error("{} 예약확정 상태전이 불가. 현재 상태: {}", reservation.getMrtReservationNo(), reservation.getReservationStatus());
            return;
        }
        if (reservation.getReservationStatus() == ReservationStatus.RESERVE_CONFIRM_PENDING) {
            checkOmhBookDetailAndUpdateReservation(reservation, message);
            return;
        }
        reservation = reservationProvider.updateOrderFormInfo(reservation.getReservationId(), bookingOrderMessageConverter.toOrderFormInfo(message));
        OmhCreateBookingRequest omhCreateBookingRequest = omhCreateBookingRequestConverter.toOmhCreateBookingRequest(reservation);
        saveCreateBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, ObjectMapperUtils.writeAsString(omhCreateBookingRequest));
        OmhCreateBookingResponse omhCreateBookingResponse;
        try {
            omhCreateBookingResponse = omhCreateBookingAgent.crateBooking(omhCreateBookingRequest);
        } catch (OmhApiException omhApiException) {
            saveCreateBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, omhApiException.getResponse());
            handleConfirmFail(reservation, message, omhApiException);
            return;
        } catch (Throwable t) {
            log.error("{} - " + ReservationSlackEvent.RESERVE_CONFIRM_RESPONSE_CHECK_FAIL.getNote(), reservation.getMrtReservationNo(), t);
            reservationSlackSender.sendToSrt(ReservationSlackEvent.RESERVE_CONFIRM_RESPONSE_CHECK_FAIL, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
            reservationProvider.confirmPending(reservation.getReservationId(), null, null);
            return;
        }
        saveCreateBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhCreateBookingResponse));
        checkOmhBookDetailAndUpdateReservation(reservation, message);
    }

    /**
     * 오마이호텔 예약상세 API 를 호출하여 현재 에약 상태를 확인하고 DB 에 업데이트한다.
     */
    private void checkOmhBookDetailAndUpdateReservation(Reservation reservation, BookingOrderMessage message) {
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, StringUtils.EMPTY);
        OmhBookingDetailResponse omhBookingDetailResponse;
        try {
            omhBookingDetailResponse = omhBookingDetailAgent.bookingDetail(reservation.getMrtReservationNo());
        } catch (OmhApiException omhApiException) {
            saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, omhApiException.getResponse());
            handleBookDetailFail(reservation, message, omhApiException);
            return;
        } catch (Throwable t) {
            handleBookDetailFail(reservation, message, t);
            return;
        }
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhBookingDetailResponse));
        updateReservationByOmhBookingDetailResponse(reservation, omhBookingDetailResponse, message);
    }

    private void updateReservationByOmhBookingDetailResponse(Reservation reservation, OmhBookingDetailResponse omhBookingDetailResponse, BookingOrderMessage message) {
        String omhBookCode = omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode();
        String hotelConfirmNo = omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo();
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.CONFIRMED) {
            reservationProvider.confirm(reservation.getReservationId(), omhBookCode, hotelConfirmNo);
            return;
        }
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.PENDING) {
            reservationProvider.confirmPending(reservation.getReservationId(), omhBookCode, hotelConfirmNo);
            reservationSlackSender.sendToSrt(ReservationSlackEvent.RESERVE_CONFIRM_PENDING, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
            return;
        }
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.UNAVAILABLE ||
            omhBookingDetailResponse.getStatus() == OmhBookingStatus.CANCELLED) {
            handleConfirmFail(reservation, message, null);
        }
    }

    private void handleConfirmFail(Reservation reservation, BookingOrderMessage message, Throwable t) {
        if (isNull(t)) {
            log.error("{} - " + ReservationSlackEvent.RESERVE_CONFIRM_FAIL.getNote(), reservation.getMrtReservationNo());
        } else {
            log.error("{} - " + ReservationSlackEvent.RESERVE_CONFIRM_FAIL.getNote(), reservation.getMrtReservationNo(), t);
        }
        reservationProvider.confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
        reservationSlackSender.sendToSrt(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
    }

    private void handleBookDetailFail(Reservation reservation, BookingOrderMessage message, Throwable t) {
        log.error("{} - " + ReservationSlackEvent.RESERVE_CONFIRM_FAIL.getNote(), reservation.getMrtReservationNo(), t);
        reservationProvider.confirmPending(reservation.getReservationId(), null, null);
        reservationSlackSender.sendToSrt(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
    }

    private void saveBookingDetailApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveBookingDetailApiLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} booking detail api log save fail", mrtReservationNo);
        }
    }

    private void saveCreateBookingApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveCreateBookingApiLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} create booking api log save fail", mrtReservationNo);
        }
    }
}
