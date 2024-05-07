package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.exception.OmhApiException;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingCodes;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackSender;
import com.myrealtrip.unionstay.common.constant.booking.BookingErrorCode;
import io.netty.handler.timeout.ReadTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReserveConfirmCheckServiceTest {

    private static final String MRT_RESERVATION_NO = "MRT_RESERVATION_NO";

    @InjectMocks
    private ReserveConfirmCheckService reserveConfirmCheckService;

    @Mock private ReservationProvider reservationProvider;
    @Mock private OmhBookingDetailAgent omhBookingDetailAgent;
    @Mock private ReservationApiLogService reservationApiLogService;
    @Mock private ReservationSlackSender reservationSlackSender;

    @Test
    @DisplayName("예약상세조회 결과 UNAVAILABLE 상태라면 RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
    void case_1() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(createOmhBookingDetailResponse(OmhBookingStatus.UNAVAILABLE));

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1)).confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
        verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, null);
    }

    @Test
    @DisplayName("예약상세조회 결과 CONFIRMED 상태라면 RESERVE_CONFIRM 처리한다.")
    void case_2() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CONFIRMED);
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1))
            .confirm(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
    }

    @Test
    @DisplayName("예약상세조회 결과 PENDING 상태라면  RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
    void case_3() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.PENDING);
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1))
            .confirmPending(reservation.getReservationId(), omhBookingDetailResponse.getBookingCodes().getOhMyBookingCode(), omhBookingDetailResponse.getBookingCodes().getHotelConfirmationNo());
        verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_PENDING, MRT_RESERVATION_NO, null);
    }

    @Test
    @DisplayName("예약상세조회 결과 CANCELLED 상태라면  RESERVE_CONFIRM_FAIL 처리하고 슬랙 알림을 보낸다.")
    void case_4() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse(OmhBookingStatus.CANCELLED);
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1))
            .confirmFail(reservation.getReservationId(), BookingErrorCode.INTERNAL_ERROR.name());
        verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.RESERVE_CONFIRM_FAIL, MRT_RESERVATION_NO, null);
    }

    @Test
    @DisplayName("예약상세조회 API 가 실패했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
    void case_6() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willThrow(OmhApiException.class);

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1))
            .confirmPending(reservation.getReservationId(), null, null);
        verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, null);
    }

    @Test
    @DisplayName("예약상세조회 API 응답을 받지 못했다면 RESERVE_CONFIRM_PENDING 처리하고 슬랙 알림을 보낸다.")
    void case_7() {
        // given
        Reservation reservation = createReservation(ReservationStatus.RESERVE_CONFIRM_PENDING);

        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willThrow(ReadTimeoutException.class);

        // when
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);

        // then
        verify(reservationProvider, times(1))
            .confirmPending(reservation.getReservationId(), null, null);
        verify(reservationSlackSender, times(1)).sendToSrtWithMention(ReservationSlackEvent.BOOK_DETAIL_API_FAIL, MRT_RESERVATION_NO, null);
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