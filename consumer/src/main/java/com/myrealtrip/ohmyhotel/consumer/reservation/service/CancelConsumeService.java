package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.consumer.reservation.converter.BookingOrderMessageConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCancelBookingAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCancelBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class CancelConsumeService {

    private final OmhCancelBookingAgent omhCancelBookingAgent;
    private final OmhBookingDetailAgent omhBookingDetailAgent;
    private final ReservationProvider reservationProvider;
    private final ReservationApiLogService reservationApiLogService;
    private final BookingOrderMessageConverter bookingOrderMessageConverter;
    private final ReservationSlackSender reservationSlackSender;
    private final String profile;

    public CancelConsumeService(OmhCancelBookingAgent omhCancelBookingAgent,
                                OmhBookingDetailAgent omhBookingDetailAgent,
                                ReservationProvider reservationProvider,
                                ReservationApiLogService reservationApiLogService,
                                BookingOrderMessageConverter bookingOrderMessageConverter,
                                ReservationSlackSender reservationSlackSender,
                                @Value("${spring.config.activate.on-profile}") String profile) {
        this.omhCancelBookingAgent = omhCancelBookingAgent;
        this.omhBookingDetailAgent = omhBookingDetailAgent;
        this.reservationProvider = reservationProvider;
        this.reservationApiLogService = reservationApiLogService;
        this.bookingOrderMessageConverter = bookingOrderMessageConverter;
        this.reservationSlackSender = reservationSlackSender;
        this.profile = profile;
    }

    @Transactional
    public void consume(BookingOrderMessage message) {
        Reservation reservation = reservationProvider.getByMrtReservationNoWithLock(message.getMrtReservationNo());
        if (!reservation.getReservationStatus().canChangeTo(ReservationStatus.CANCEL_SUCCESS)) {
            log.error("{} - 예약확정 상태전이 불가. 현재 상태: {}", reservation.getMrtReservationNo(), reservation.getReservationStatus());
            return;
        }
        // 예약상세 API 조회하여 취소되어 있는지 여부 확인
        OmhBookingDetailResponse omhBookingDetailResponse = omhBookingDetail(reservation);
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.CANCELLED) {
            handleCancelSuccess(reservation, message, null, omhBookingDetailResponse.getAmount().getTotalNetAmount());
            return;
        }
        omhBookingCancel(reservation, message);
    }

    private OmhBookingDetailResponse omhBookingDetail(Reservation reservation) {
        OmhBookingDetailResponse omhBookingDetailResponse = omhBookingDetailAgent.bookingDetail(reservation.getMrtReservationNo());
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, StringUtils.EMPTY);
        saveBookingDetailApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhBookingDetailResponse));
        return omhBookingDetailResponse;
    }

    private void omhBookingCancel(Reservation reservation, BookingOrderMessage message) {
        saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, StringUtils.EMPTY);
        OmhCancelBookingResponse omhCancelBookingResponse;
        try {
            omhCancelBookingResponse = omhCancelBookingAgent.cancelBooking(reservation.getMrtReservationNo());
        } catch (OmhApiException omhApiException) {
            log.error("{} - 예약 취소 API 실패", message.getMrtReservationNo());
            saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, omhApiException.getResponse());
            handleCancelFail(reservation, message, omhApiException.toString());
            return;
        } catch (Throwable t) {
            log.error("{} - 예약 취소 API 실패", message.getMrtReservationNo());
            handleCancelFail(reservation, message, t.toString());
            return;
        }
        saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhCancelBookingResponse));

        // 예약상세 API 조회하여 취소되어 있는지 여부 확인
        OmhBookingDetailResponse omhBookingDetailResponse = omhBookingDetail(reservation);
        if (omhBookingDetailResponse.getStatus() == OmhBookingStatus.CANCELLED) {
            handleCancelSuccess(reservation, message, omhCancelBookingResponse.getCancelConfirmNo(), omhBookingDetailResponse.getAmount().getTotalNetAmount());
        } else {
            log.error("{} - 예약 취소 API 는 성공했지만 에약조회 결과 CANCELLED 상태가 아닙니다.", message.getMrtReservationNo());
            handleCancelFail(reservation, message, "예약 취소 API 는 성공했지만 에약조회 결과 CANCELLED 상태가 아닙니다.");
        }
    }

    public void handleCancelFail(Reservation reservation, BookingOrderMessage message, String errorMessage) {
        reservationProvider.cancelFail(
            reservation.getReservationId(),
            bookingOrderMessageConverter.toCanceledBy(message),
            message.getCancelReason(),
            message.getCancelReasonType(),
            errorMessage,
            BookingErrorCode.INTERNAL_ERROR.name()
        );
        reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.CANCEL_FAIL, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
    }

    public void handleCancelSuccess(Reservation reservation, BookingOrderMessage message, String cancelConfirmNo, BigDecimal omhCancelPenaltyDepositPrice) {
        BigDecimal cancelPenaltyDepositPrice;
        BigDecimal cancelPenaltySalePrice;
        if (!profile.contains("stage") && !profile.contains("prod")) {
            // 개발 환경에서는 오마이호텔 cancelPenaltyAmount 가 null 로 반환되므로 요청들어온 환불가격 그대로 사용한다.
            cancelPenaltySalePrice = reservation.getSalePrice().subtract(message.getCancelRefundAmountChecked());
            cancelPenaltyDepositPrice = OmhPriceCalculateUtils.reverseToDepositPrice(cancelPenaltySalePrice, reservation.getMrtCommissionRate());
        } else {
            cancelPenaltyDepositPrice = nonNull(omhCancelPenaltyDepositPrice) ?
                                        omhCancelPenaltyDepositPrice :
                                        BigDecimal.ZERO;
            cancelPenaltySalePrice = OmhPriceCalculateUtils.toSalePrice(cancelPenaltyDepositPrice, reservation.getMrtCommissionRate());
        }
        reservationProvider.cancelSuccess(
            reservation.getReservationId(),
            bookingOrderMessageConverter.toCanceledBy(message),
            message.getCancelReason(),
            message.getCancelReasonType(),
            cancelConfirmNo,
            cancelPenaltyDepositPrice,
            cancelPenaltySalePrice
        );
    }

    private void saveBookingDetailApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveBookingDetailForCancelCheckLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} - booking detail api log save fail", mrtReservationNo);
        }
    }

    private void saveCancelBookingApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveCancelBookingLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} - cancel booking api log save fail", mrtReservationNo);
        }
    }
}
