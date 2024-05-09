package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.consumer.reservation.converter.BookingOrderMessageConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCancelBookingAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingDetailAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCancelBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import com.myrealtrip.unionstay.common.constant.booking.OrderUserType;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CancelConsumeServiceTest {

    private static final String MRT_RESERVATION_NO = "MRT_RESERVATION_NO";

    private CancelConsumeService devCancelConsumeService;
    private CancelConsumeService prodCancelConsumeService;

    @Mock private OmhCancelBookingAgent omhCancelBookingAgent;
    @Mock private ReservationProvider reservationProvider;
    @Mock private ReservationApiLogService reservationApiLogService;
    @Spy private BookingOrderMessageConverter bookingOrderMessageConverter;
    @Mock private ReservationSlackSender reservationSlackSender;
    @Mock private OmhBookingDetailAgent omhBookingDetailAgent;

    @BeforeEach
     void beforeEach() {
        devCancelConsumeService = new CancelConsumeService(omhCancelBookingAgent, omhBookingDetailAgent, reservationProvider, reservationApiLogService, bookingOrderMessageConverter, reservationSlackSender, "dev");
        prodCancelConsumeService = new CancelConsumeService(omhCancelBookingAgent, omhBookingDetailAgent, reservationProvider, reservationApiLogService, bookingOrderMessageConverter, reservationSlackSender, "prod");
    }

    @Test
    @DisplayName("예약상태가 CANCEL_SUCCESS 라면 아무것도 하지 않고 종료한다.")
    void case_1() {
        // given
        Reservation reservation = createReservation(ReservationStatus.CANCEL_SUCCESS);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
              .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        // when
        prodCancelConsumeService.consume(message);

        // then
        verify(omhCancelBookingAgent, never()).cancelBooking(any());
    }

    @Test
    @DisplayName("오마이호텔에서 이미 취소된 예약이라면 예약취소성공 상태로 변경한다.")
    void case_1_2() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED, BigDecimal.valueOf(0));
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        // when
        prodCancelConsumeService.consume(message);

        // then
        verify(reservationProvider, times(1))
            .cancelSuccess(reservation.getReservationId(), CanceledBy.TRAVELER, message.getCancelReason(), message.getCancelReasonType(), null, BigDecimal.valueOf(0), BigDecimal.valueOf(0));
    }

    @Test
    @DisplayName("오마이호텔 예약취소 API 에 성공했다면 예약취소성공 상태로 변경한다.")
    void case_2() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED, BigDecimal.valueOf(10000));
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        OmhCancelBookingResponse omhCancelBookingResponse = createOmhCancelBookingResponse(BigDecimal.valueOf(10000));
        given(omhCancelBookingAgent.cancelBooking(MRT_RESERVATION_NO))
            .willReturn(omhCancelBookingResponse);

        // when
        prodCancelConsumeService.consume(message);

        // then
        verify(reservationProvider, times(1))
            .cancelSuccess(reservation.getReservationId(), CanceledBy.TRAVELER, message.getCancelReason(), message.getCancelReasonType(), omhCancelBookingResponse.getCancelConfirmNo(), BigDecimal.valueOf(10000), BigDecimal.valueOf(11000));
    }

    @Test
    @DisplayName("오마이호텔 예약취소 API 에 성공했다면 예약취소성공 상태로 변경한다. 개발환경이라면 메세지로 들어온 환불금액을 그대로 사용한다.")
    void case_3() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED, BigDecimal.valueOf(10000));
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        OmhCancelBookingResponse omhCancelBookingResponse = createOmhCancelBookingResponse(null);
        given(omhCancelBookingAgent.cancelBooking(MRT_RESERVATION_NO))
            .willReturn(omhCancelBookingResponse);

        // when
        devCancelConsumeService.consume(message);

        // then
        verify(reservationProvider, times(1))
            .cancelSuccess(reservation.getReservationId(), CanceledBy.TRAVELER, message.getCancelReason(), message.getCancelReasonType(), omhCancelBookingResponse.getCancelConfirmNo(), BigDecimal.valueOf(10000), BigDecimal.valueOf(11000));
    }

    @Test
    @DisplayName("오마이호텔 예약취소 API 가 실패했다면 예약취소실패 상태로 변경하고 슬랙알림을 보낸다.")
    void case_4() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED, BigDecimal.valueOf(10000));
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        given(omhCancelBookingAgent.cancelBooking(MRT_RESERVATION_NO))
            .willThrow(OmhApiException.class);

        // when
        devCancelConsumeService.consume(message);

        // then
        verify(reservationProvider, times(1))
            .cancelFail(eq(reservation.getReservationId()), eq(CanceledBy.TRAVELER), eq(message.getCancelReason()), eq(message.getCancelReasonType()), any(), eq(BookingErrorCode.INTERNAL_ERROR.name()));

        verify(reservationSlackSender, times(1))
            .sendToSrtWithMention(ReservationSlackEvent.CANCEL_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
    }

    @Test
    @DisplayName("오마이호텔 예약취소 API 응답을 받지 못했다면 예약취소실패 상태로 변경하고 슬랙알림을 보낸다.")
    void case_5() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);
        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        BookingOrderMessage message = createMessage(BigDecimal.valueOf(0));

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED, BigDecimal.valueOf(10000));
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        given(omhCancelBookingAgent.cancelBooking(MRT_RESERVATION_NO))
            .willThrow(ReadTimeoutException.class);

        // when
        devCancelConsumeService.consume(message);

        // then
        verify(reservationProvider, times(1))
            .cancelFail(eq(reservation.getReservationId()), eq(CanceledBy.TRAVELER), eq(message.getCancelReason()), eq(message.getCancelReasonType()), any(), eq(BookingErrorCode.INTERNAL_ERROR.name()));

        verify(reservationSlackSender, times(1))
            .sendToSrtWithMention(ReservationSlackEvent.CANCEL_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
    }

    private OmhBookingDetailResponse createOmhBookingDetailResponse(OmhBookingStatus status, BigDecimal totalNetAmount) {
        return OmhBookingDetailResponse.builder()
            .status(status)
            .amount(OmhBookingDetailAmount.builder().totalNetAmount(totalNetAmount).build())
            .build();
    }

    private OmhCancelBookingResponse createOmhCancelBookingResponse(BigDecimal cancelPenaltyAmount) {
        return OmhCancelBookingResponse.builder()
            .cancelConfirmNo("")
            .currencyCode("KRW")
            .cancelPenaltyAmount(cancelPenaltyAmount)
            .build();
    }

    private BookingOrderMessage createMessage(BigDecimal cancelRefundAmount) {
        return BookingOrderMessage.builder()
            .mrtReservationNo(MRT_RESERVATION_NO)
            .orderUserType(OrderUserType.TRAVELER)
            .cancelReason("")
            .cancelReasonType("")
            .cancelRefundAmountChecked(cancelRefundAmount)
            .build();
    }

    private Reservation createReservation(ReservationStatus status) {
        return Reservation.builder()
            .reservationId(999L)
            .mrtReservationNo(MRT_RESERVATION_NO)
            .reservationStatus(status)
            .hotelId(1L)
            .checkInDate(LocalDate.of(2023, 1, 1))
            .checkOutDate(LocalDate.of(2023, 1, 1))
            .roomTypeCode("roomTypeCode")
            .ratePlanCode("ratePlanCode")
            .depositPrice(BigDecimal.valueOf(10000))
            .salePrice(BigDecimal.valueOf(11000))
            .mrtCommissionRate(BigDecimal.valueOf(10))
            .build();
    }
}