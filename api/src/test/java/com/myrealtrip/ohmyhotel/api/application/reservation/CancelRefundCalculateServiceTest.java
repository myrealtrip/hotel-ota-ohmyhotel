package com.myrealtrip.ohmyhotel.api.application.reservation;

import com.myrealtrip.ohmyhotel.api.application.reservation.converter.CancelRefundResponseConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingStatusConverter;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingCancelPolicyValue;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse.OmhBookingDetailCancelPolicy;
import com.myrealtrip.ohmyhotel.utils.DateTimeUtils;
import com.myrealtrip.unionstay.dto.hotelota.booking.response.ItineraryCancelRefundResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class CancelRefundCalculateServiceTest {

    public static final String MRT_RESERVATION_NO = "MRT";

    @InjectMocks
    private CancelRefundCalculateService cancelRefundCalculateService;

    @Mock private OmhBookingDetailAgent omhBookingDetailAgent;
    @Mock private ReservationProvider reservationProvider;
    @Mock private ReservationApiLogService reservationApiLogService;

    @Spy
    private CancelRefundResponseConverter cancelRefundResponseConverter = new CancelRefundResponseConverter(new BookingStatusConverter());

    private MockedStatic<DateTimeUtils> dateTimeUtils;

    @BeforeEach
    void beforeEach() {
        dateTimeUtils = Mockito.mockStatic(DateTimeUtils.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
    }

    @AfterEach
    void afterEach() {
        dateTimeUtils.close();
    }

    @Test
    @DisplayName("nonRefundable=true 일 떄 테스트 -> 환불금액 0원")
    void nonRefundable_true() {
        // given
        Reservation reservation = createReservation();
        given(reservationProvider.getByMrtReservationNoReadOnly(MRT_RESERVATION_NO))
            .willReturn(reservation);

        OmhBookingDetailResponse omhBookingDetailResponse = OmhBookingDetailResponse.builder()
            .cancellationPolicy(OmhBookingDetailCancelPolicy.builder().isNonRefundable(true).build())
            .build();
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        // when
        ItineraryCancelRefundResponse cancelRefundResponse = cancelRefundCalculateService.getCancelRefund(MRT_RESERVATION_NO);

        // then
        assertThat(cancelRefundResponse.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(cancelRefundResponse.getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));

        assertThat(cancelRefundResponse.getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    @DisplayName("현재 날짜가 policy 구간 안에 속할 때 부분환불 테스트")
    void in_policy() {
        // given
        Reservation reservation = createReservation();
        given(reservationProvider.getByMrtReservationNoReadOnly(MRT_RESERVATION_NO))
            .willReturn(reservation);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse();
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        given(DateTimeUtils.now(ZoneId.of(omhBookingDetailResponse.getCancellationPolicy().getTimeZone())))
            .willReturn(LocalDateTime.of(2023, 1, 5, 12, 0, 0));

        // when
        ItineraryCancelRefundResponse cancelRefundResponse = cancelRefundCalculateService.getCancelRefund(MRT_RESERVATION_NO);

        // then
        assertThat(cancelRefundResponse.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(52));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(52));

        assertThat(cancelRefundResponse.getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(78));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(78));

        assertThat(cancelRefundResponse.getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(18));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(18));
    }

    @Test
    @DisplayName("현재 날짜가 policy 구간보다 이전 일때 -> 전액환불")
    void before_policy() {
        // given
        Reservation reservation = createReservation();
        given(reservationProvider.getByMrtReservationNoReadOnly(MRT_RESERVATION_NO))
            .willReturn(reservation);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse();
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        given(DateTimeUtils.now(ZoneId.of(omhBookingDetailResponse.getCancellationPolicy().getTimeZone())))
            .willReturn(LocalDateTime.of(2023, 1, 4, 23, 59, 59));

        // when
        ItineraryCancelRefundResponse cancelRefundResponse = cancelRefundCalculateService.getCancelRefund(MRT_RESERVATION_NO);

        // then
        assertThat(cancelRefundResponse.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));

        assertThat(cancelRefundResponse.getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(cancelRefundResponse.getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("현재 날짜가 policy 구간보다 이후 일때 -> 환불금액 0원")
    void after_policy() {
        // given
        Reservation reservation = createReservation();
        given(reservationProvider.getByMrtReservationNoReadOnly(MRT_RESERVATION_NO))
            .willReturn(reservation);

        OmhBookingDetailResponse omhBookingDetailResponse = createOmhBookingDetailResponse();
        given(omhBookingDetailAgent.bookingDetail(MRT_RESERVATION_NO))
            .willReturn(omhBookingDetailResponse);

        given(DateTimeUtils.now(ZoneId.of(omhBookingDetailResponse.getCancellationPolicy().getTimeZone())))
            .willReturn(LocalDateTime.of(2023, 1, 6, 0, 0, 0));

        // when
        ItineraryCancelRefundResponse cancelRefundResponse = cancelRefundCalculateService.getCancelRefund(MRT_RESERVATION_NO);

        // then
        assertThat(cancelRefundResponse.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        assertThat(cancelRefundResponse.getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getTotalCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(130));

        assertThat(cancelRefundResponse.getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
        assertThat(cancelRefundResponse.getRoomCancelRefunds().get(0).getMrtCancelCommissionAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    private Reservation createReservation() {
        return Reservation.builder()
            .mrtReservationNo(MRT_RESERVATION_NO)
            .reservationId(1L)
            .salePrice(BigDecimal.valueOf(130))
            .depositPrice(BigDecimal.valueOf(100))
            .mrtCommissionRate(BigDecimal.valueOf(30))
            .reservationStatus(ReservationStatus.RESERVE_CONFIRM)
            .build();
    }

    private OmhBookingDetailResponse createOmhBookingDetailResponse() {
        OmhBookingDetailCancelPolicy omhBookingDetailCancelPolicy = OmhBookingDetailCancelPolicy.builder()
            .isNonRefundable(false)
            .timeZone("Asia/Seoul")
            .policies(List.of(OmhBookingCancelPolicyValue.builder()
                                  .fromDateTime("2023-01-05 00:00:00")
                                  .toDateTime("2023-01-05 23:59:59")
                                  .penaltyAmount(BigDecimal.valueOf(60))
                                  .build()))
            .build();
        return OmhBookingDetailResponse.builder()
            .cancellationPolicy(omhBookingDetailCancelPolicy)
            .build();
    }
}