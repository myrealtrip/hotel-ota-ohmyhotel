package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.consumer.reservation.converter.BookingOrderMessageConverter;
import com.myrealtrip.ohmyhotel.consumer.reservation.converter.OmhCreateBookingRequestConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCreateBookingAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingCodes;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import com.myrealtrip.unionstay.dto.hotelota.booking.request.CustomerDetail;
import com.myrealtrip.unionstay.dto.hotelota.booking.request.GuestDetail;
import com.myrealtrip.unionstay.dto.hotelota.booking.request.RoomBooking;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReserveConfirmConsumeServiceTest {

    private static final String MRT_RESERVATION_NO = "MRT_RESERVATION_NO";

    @InjectMocks
    private ReserveConfirmConsumeService reserveConfirmConsumeService;

    @Mock private ReservationProvider reservationProvider;
    @Mock private OmhBookingDetailAgent omhBookingDetailAgent;
    @Mock private OmhCreateBookingAgent omhCreateBookingAgent;
    @Mock private ReservationApiLogService reservationApiLogService;
    @Mock private ReservationSlackSender reservationSlackSender;

    @Spy private BookingOrderMessageConverter bookingOrderMessageConverter;
    @Spy private OmhCreateBookingRequestConverter omhCreateBookingRequestConverter;

    @Test
    @DisplayName("현재 예약이 이미 RESERVE_CONFIRM 상태라면 아무것도 하지 않고 종료한다.")
    void case_1() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM);

        given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
            .willReturn(reservation);

        // when
        reserveConfirmConsumeService.consumeReserveConfirm(createMessage());

        // then
        verify(omhCreateBookingAgent, never()).crateBooking(any());
        verify(omhBookingDetailAgent, never()).bookingDetail(any());
    }

    @Nested
    @DisplayName("현재 예약이 RESERVE_PENDING 상태라면 오마이호텔 예약상세조회 API 를 조회한다.")
    class RESERVE_PENDING {

        @Test
        @DisplayName("예약상세조회 결과 UNAVAILABLE 상태라면 RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
        void case_2() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(createOmhBookingDetailResponse(OmhBookingStatus.UNAVAILABLE));

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1)).confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약상세조회 결과 CONFIRMED 상태라면 RESERVE_CONFIRM 처리한다.")
        void case_3() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirm(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
        }

        @Test
        @DisplayName("예약상세조회 결과 PENDING 상태라면  RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_4() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.PENDING);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_PENDING, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약상세조회 결과 CANCELLED 상태라면  RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
        void case_5() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약상세조회 API 가 실패했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_6() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willThrow(OmhApiException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), null, null);
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약상세조회 API 응답을 받지 못했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_6_2() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willThrow(ReadTimeoutException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), null, null);
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }
    }

    @Nested
    @DisplayName("현재 예약이 PRECHECK_SUCCESS 상태라면 오마이호텔 예약확정 API 를 호출한다.")
    class PRECHECK_SUCCESS {

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 결과 UNAVAILABLE 상태라면 RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
        void case_7() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(createOmhBookingDetailResponse(OmhBookingStatus.UNAVAILABLE));

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1)).confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 결과 CONFIRMED 상태라면 RESERVE_CONFIRM 처리한다.")
        void case_8() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirm(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
        }

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 결과 PENDING 상태라면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_9() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.PENDING);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_PENDING, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 결과 CANCELLED 상태라면  RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
        void case_10() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 API 가 실패했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_11() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willThrow(OmhApiException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), null, null);
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 가 성공했다면 오마이호텔 예약상세조회 API 를 호출한다. " +
                     "예약상세조회 API 응답을 받지 못했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_11_2() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willReturn(createOmhCreateBookingResponse());

            OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED);
            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willReturn(omhBookingDetailResponse);

            given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
                .willThrow(ReadTimeoutException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), null, null);
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.OMH_BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 가 실패했다면 RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
        void case_12() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willThrow(OmhApiException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }

        @Test
        @DisplayName("예약확정 API 응답을 받지 못했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
        void case_13() {
            // given
            BookingOrderMessage message = createMessage();
            Reservation reservation = createReservation(ReservationStatus.PRECHECK_SUCCESS);

            given(reservationProvider.getByMrtReservationNoWithLock(MRT_RESERVATION_NO))
                .willReturn(reservation);

            given(reservationProvider.updateOrderFormInfo(eq(reservation.getReservationId()), any()))
                .willReturn(reservation);

            given(omhCreateBookingAgent.crateBooking(any()))
                .willThrow(ReadTimeoutException.class);

            // when
            reserveConfirmConsumeService.consumeReserveConfirm(message);

            // then
            verify(reservationProvider, times(1))
                .confirmPending(reservation.getReservationId(), null, null);
            verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_RESPONSE_CHECK_FAIL, MRT_RESERVATION_NO, ObjectMapperUtils.writeAsString(message));
        }
    }

    private OmhCreateBookingResponse createOmhCreateBookingResponse() {
        return OmhCreateBookingResponse.builder().build();
    }

    private OmhBookingDetailResponse createOmhBookingDetailResponse(OmhBookingStatus status) {
        return OmhBookingDetailResponse.builder()
            .status(status)
            .bookingCodes(OmhBookingCodes.builder()
                              .ohMyBookingCode("ohMyBookingCode")
                              .hotelConfirmationNo("hotelConfirmationNo")
                              .build())
            .build();
    }

    private BookingOrderMessage createMessage() {
        RoomBooking roomBooking = RoomBooking.builder()
            .guestDetail(GuestDetail.builder()
                             .firstName("firstName")
                             .lastName("lastName")
                             .email("email")
                             .number("number")
                             .build())
            .build();

        CustomerDetail customerDetail = CustomerDetail.builder()
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .number("number")
            .build();
        return BookingOrderMessage.builder()
            .mrtReservationNo(MRT_RESERVATION_NO)
            .rooms(List.of(roomBooking))
            .customerDetail(customerDetail)
            .build();
    }

    private Reservation createReservation(ReservationStatus status) {
        return Reservation.builder()
            .reservationId(999L)
            .mrtReservationNo(MRT_RESERVATION_NO)
            .reservationStatus(status)
            .checkInUser(com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail.builder()
                             .firstName("firstName")
                             .lastName("lastName")
                             .email("email")
                             .contact("number")
                             .build())
            .hotelId(1L)
            .checkInDate(LocalDate.of(2023, 1, 1))
            .checkOutDate(LocalDate.of(2023, 1, 1))
            .roomTypeCode("roomTypeCode")
            .ratePlanCode("ratePlanCode")
            .guestCount(GuestCount.builder()
                            .adultCount(2)
                            .childCount(0)
                            .childAges(Collections.emptyList())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder()
                                .rateType(RateType.STANDARD_RATE)
                                .build())
            .depositPrice(BigDecimal.valueOf(10000))
            .build();
    }
}