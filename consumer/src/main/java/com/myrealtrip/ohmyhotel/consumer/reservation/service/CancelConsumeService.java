package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.consumer.reservation.converter.BookingOrderMessageConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCancelBookingAgent;
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
    private final ReservationProvider reservationProvider;
    private final ReservationApiLogService reservationApiLogService;
    private final BookingOrderMessageConverter bookingOrderMessageConverter;
    private final ReservationSlackSender reservationSlackSender;
    private final String profile;

    public CancelConsumeService(OmhCancelBookingAgent omhCancelBookingAgent,
                                ReservationProvider reservationProvider,
                                ReservationApiLogService reservationApiLogService,
                                BookingOrderMessageConverter bookingOrderMessageConverter,
                                ReservationSlackSender reservationSlackSender,
                                @Value("${spring.config.activate.on-profile}") String profile) {
        this.omhCancelBookingAgent = omhCancelBookingAgent;
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
        // TODO 예약상세 API 조회하여 이미 취소되어 있는지 여부 확인 -> 오마이호텔 이메일 답변 오면 작업
        saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, StringUtils.EMPTY);
        OmhCancelBookingResponse omhCancelBookingResponse;
        try {
            omhCancelBookingResponse = omhCancelBookingAgent.cancelBooking(reservation.getMrtReservationNo());
        } catch (OmhApiException omhApiException) {
            saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.REQUEST, omhApiException.getResponse());
            handleCancelFail(reservation, message, omhApiException);
            return;
        } catch (Throwable t) {
            handleCancelFail(reservation, message, t);
            return;
        }
        saveCancelBookingApiLog(reservation.getMrtReservationNo(), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhCancelBookingResponse));
        handleCancelSuccess(reservation, message, omhCancelBookingResponse);
    }

    private void saveCancelBookingApiLog(String mrtReservationNo, ApiLogType logType, String logStr) {
        try {
            reservationApiLogService.saveCancelBookingLog(mrtReservationNo, logType, logStr);
        } catch (Throwable t) {
            log.error("{} - cancel booking api log save fail", mrtReservationNo);
        }
    }

    public void handleCancelFail(Reservation reservation, BookingOrderMessage message, Throwable t) {
        reservationProvider.cancelFail(
            reservation.getReservationId(),
            bookingOrderMessageConverter.toCanceledBy(message),
            message.getCancelReason(),
            message.getCancelReasonType(),
            t.toString(),
            BookingErrorCode.INTERNAL_ERROR.name()
        );
        reservationSlackSender.sendToSrtWithMention(ReservationSlackEvent.CANCEL_FAIL, reservation.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
    }

    public void handleCancelSuccess(Reservation reservation, BookingOrderMessage message, OmhCancelBookingResponse omhCancelBookingResponse) {
        BigDecimal cancelPenaltyDepositPrice;
        BigDecimal cancelPenaltySalePrice;
        if (!profile.contains("stage") || !profile.contains("prod")) {
            // 개봘 환경에서는 오마이호텔 cancelPenaltyAmount 가 null 로 반환되므로 요청들어온 환불가격 그대로 사용한다.
            cancelPenaltySalePrice = reservation.getSalePrice().subtract(message.getCancelRefundAmountChecked());
            cancelPenaltyDepositPrice = OmhPriceCalculateUtils.reverseToDepositPrice(cancelPenaltySalePrice, reservation.getMrtCommissionRate());
        } else {
            cancelPenaltyDepositPrice = nonNull(omhCancelBookingResponse.getCancelPenaltyAmount()) ?
                                        omhCancelBookingResponse.getCancelPenaltyAmount() :
                                        BigDecimal.ZERO;
            cancelPenaltySalePrice = OmhPriceCalculateUtils.toSalePrice(cancelPenaltyDepositPrice, reservation.getMrtCommissionRate());
        }
        reservationProvider.cancelSuccess(
            reservation.getReservationId(),
            bookingOrderMessageConverter.toCanceledBy(message),
            message.getCancelReason(),
            message.getCancelReasonType(),
            omhCancelBookingResponse.getCancelConfirmNo(),
            cancelPenaltyDepositPrice,
            cancelPenaltySalePrice
        );
    }
}
