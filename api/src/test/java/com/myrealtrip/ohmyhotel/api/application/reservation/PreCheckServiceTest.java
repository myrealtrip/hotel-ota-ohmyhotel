package com.myrealtrip.ohmyhotel.api.application.reservation;

import com.myrealtrip.ohmyhotel.core.service.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.PreCheckRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.PreCheckResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.ReservationConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.OrderProvider;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.OmhPreCheckStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhPreCheckAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse.OmhPreCheckAmount;
import com.myrealtrip.unionstay.common.constant.booking.PreCheckStatus;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.precheck.response.PreCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PreCheckServiceTest {

    @InjectMocks
    private PreCheckService preCheckService;

    @Mock private ReservationApiLogService reservationApiLogService;
    @Mock private OrderProvider orderProvider;
    @Mock private ReservationProvider reservationProvider;
    @Mock private OmhPreCheckAgent omhPreCheckAgent;

    @Spy private PreCheckRequestConverter preCheckRequestConverter;
    @Spy private PreCheckResponseConverter preCheckResponseConverter;
    @Spy private ReservationConverter reservationConverter;

    @Test
    @DisplayName("preCheck 실패 테스트 - sold out")
    void pre_check_fail_1() {
        // given
        PreCheckRequest preCheckRequest = createPreCheckRequest();

        Order order = createOrder();
        given(orderProvider.getByOrderId(Long.valueOf(preCheckRequest.getPreCheckApiKey())))
            .willReturn(order);

        OmhPreCheckResponse omhPreCheckResponse = OmhPreCheckResponse.builder()
            .status(OmhPreCheckStatus.SOLD_OUT)
            .build();
        given(omhPreCheckAgent.preCheck(any()))
            .willReturn(omhPreCheckResponse);

        given(reservationProvider.getByMrtReservationNo(any()))
            .willReturn(null);

        // when
        PreCheckResponse preCheckResponse = preCheckService.preCheck(preCheckRequest);

        // then
        // API 로그 저장여부 검증
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.REQUEST), any());
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.RESPONSE), any());

        // 예약 데이터 검증
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationProvider, times(1)).upsert(reservationArgumentCaptor.capture());
        assertThat(reservationArgumentCaptor.getValue().getReservationStatus()).isEqualTo(ReservationStatus.PRECHECK_FAIL);

        // 응답결과 검증
        assertThat(preCheckResponse.getStatus()).isEqualTo(PreCheckStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("preCheck 실패 테스트 - price change")
    void pre_check_fail_2() {
        // given
        PreCheckRequest preCheckRequest = createPreCheckRequest();

        Order order = createOrder();
        given(orderProvider.getByOrderId(Long.valueOf(preCheckRequest.getPreCheckApiKey())))
            .willReturn(order);

        OmhPreCheckResponse omhPreCheckResponse = OmhPreCheckResponse.builder()
            .status(OmhPreCheckStatus.AMOUNT_CHANGED)
            .build();
        given(omhPreCheckAgent.preCheck(any()))
            .willReturn(omhPreCheckResponse);

        given(reservationProvider.getByMrtReservationNo(any()))
            .willReturn(null);

        // when
        PreCheckResponse preCheckResponse = preCheckService.preCheck(preCheckRequest);

        // then
        // API 로그 저장여부 검증
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.REQUEST), any());
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.RESPONSE), any());

        // 예약 데이터 검증
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationProvider, times(1)).upsert(reservationArgumentCaptor.capture());
        assertThat(reservationArgumentCaptor.getValue().getReservationStatus()).isEqualTo(ReservationStatus.PRECHECK_FAIL);

        // 응답결과 검증
        assertThat(preCheckResponse.getStatus()).isEqualTo(PreCheckStatus.PRICE_CHANGED);
    }

    @Test
    @DisplayName("preCheck 성공 테스트")
    void pre_check_success() {
        // given
        PreCheckRequest preCheckRequest = createPreCheckRequest();

        Order order = createOrder();
        given(orderProvider.getByOrderId(Long.valueOf(preCheckRequest.getPreCheckApiKey())))
            .willReturn(order);

        OmhPreCheckResponse omhPreCheckResponse = OmhPreCheckResponse.builder()
            .amount(OmhPreCheckAmount.builder()
                        .totalNetAmount(BigDecimal.valueOf(100))
                        .build())
            .status(OmhPreCheckStatus.AVAILABLE)
            .build();
        given(omhPreCheckAgent.preCheck(any()))
            .willReturn(omhPreCheckResponse);

        given(reservationProvider.getByMrtReservationNo(any()))
            .willReturn(null);

        // when
        PreCheckResponse preCheckResponse = preCheckService.preCheck(preCheckRequest);

        // then
        // API 로그 저장여부 검증
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.REQUEST), any());
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.RESPONSE), any());

        // 예약 데이터 검증
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationProvider, times(1)).upsert(reservationArgumentCaptor.capture());
        assertThat(reservationArgumentCaptor.getValue().getReservationStatus()).isEqualTo(ReservationStatus.PRECHECK_SUCCESS);
        assertThat(reservationArgumentCaptor.getValue().getReservationId()).isEqualTo(null);

        // 응답결과 검증
        assertThat(preCheckResponse.getStatus()).isEqualTo(PreCheckStatus.AVAILABLE);
    }

    @Test
    @DisplayName("preCheck 성공 테스트 - 기존 예약번호 재활용")
    void pre_check_success_2() {
        // given
        PreCheckRequest preCheckRequest = createPreCheckRequest();

        Order order = createOrder();
        given(orderProvider.getByOrderId(Long.valueOf(preCheckRequest.getPreCheckApiKey())))
            .willReturn(order);

        OmhPreCheckResponse omhPreCheckResponse = OmhPreCheckResponse.builder()
            .amount(OmhPreCheckAmount.builder()
                        .totalNetAmount(BigDecimal.valueOf(100))
                        .build())
            .status(OmhPreCheckStatus.AVAILABLE)
            .build();
        given(omhPreCheckAgent.preCheck(any()))
            .willReturn(omhPreCheckResponse);

        Reservation reservation = Reservation.builder()
            .reservationId(99L)
            .mrtReservationNo(preCheckRequest.getMrtOrderNumber())
            .reservationStatus(ReservationStatus.PRECHECK_FAIL)
            .build();
        given(reservationProvider.getByMrtReservationNo(preCheckRequest.getMrtOrderNumber()))
            .willReturn(reservation);

        // when
        PreCheckResponse preCheckResponse = preCheckService.preCheck(preCheckRequest);

        // then
        // API 로그 저장여부 검증
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.REQUEST), any());
        verify(reservationApiLogService, times(1)).savePreCheckApiLog(eq(preCheckRequest.getMrtOrderNumber()), eq(ApiLogType.RESPONSE), any());

        // 예약 데이터 검증
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationProvider, times(1)).upsert(reservationArgumentCaptor.capture());
        assertThat(reservationArgumentCaptor.getValue().getReservationStatus()).isEqualTo(ReservationStatus.PRECHECK_SUCCESS);
        assertThat(reservationArgumentCaptor.getValue().getReservationId()).isEqualTo(reservation.getReservationId());

        // 응답결과 검증
        assertThat(preCheckResponse.getStatus()).isEqualTo(PreCheckStatus.AVAILABLE);
    }

    private PreCheckRequest createPreCheckRequest() {
        return PreCheckRequest.builder()
            .preCheckApiKey("1")
            .mrtOrderNumber("mrtReservationNo")
            .checkin(LocalDate.of(2023, 1, 1))
            .checkout(LocalDate.of(2023, 1, 2))
            .propertyId("10")
            .roomId("100")
            .rateId("1000")
            .adultCount(2)
            .childCount(0)
            .childAges(Collections.emptyList())
            .build();
    }

    private Order createOrder() {
        return Order.builder()
            .hotelId(10L)
            .hotelName("hotelName")
            .roomTypeCode("100")
            .roomTypeName("roomTypeName")
            .ratePlanCode("1000")
            .ratePlanName("ratePlanName")
            .checkInDate(LocalDate.now())
            .checkOutDate(LocalDate.now())
            .salePrice(BigDecimal.valueOf(110))
            .depositPrice(BigDecimal.valueOf(100))
            .zeroMarginApply(false)
            .zeroMarginApplyPrice(null)
            .mrtCommissionRate(BigDecimal.valueOf(10))
            .guestCount(GuestCount.builder()
                            .adultCount(2)
                            .childCount(0)
                            .childAges(List.of())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder().build())
            .build();
    }
}