package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.application.common.BedDescriptionConverter;
import com.myrealtrip.ohmyhotel.api.application.common.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.api.application.search.converter.CommonSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.OrderConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.provider.reservation.OrderProvider;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.BedTypeCode;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup.OmhBed;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.unionstay.common.constant.CountryCode;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderSearchServiceTest {

    @InjectMocks
    private OrderSearchService orderSearchService;

    @Spy private SearchRequestConverter searchRequestConverter;
    @Spy private SingleSearchResponseConverter singleSearchResponseConverter =
        new SingleSearchResponseConverter(new CommonSearchResponseConverter(), new BedDescriptionConverter());

    @Mock private ReservationApiLogService reservationApiLogService;
    @Mock private CommissionRateService commissionRateService;
    @Mock private OrderProvider orderProvider;
    @Mock private OmhRoomsAvailabilityAgent omhRoomsAvailabilityAgent;
    @Mock private OrderConverter orderConverter;

    @Test
    @DisplayName("한 개 이상의 호텔이 요청으로 들어오면 Exception 을 던진다.")
    void multiple_hotel_throw() {
        // given
        SearchRequest searchRequest = SearchRequest.builder()
            .countryCode(CountryCode.KR)
            .propertyIds(List.of("1", "2"))
            .checkin(LocalDate.of(2023, 5, 10))
            .checkout(LocalDate.of(2023, 5, 12))
            .adultCount(2)
            .childCount(0)
            .childAges(Collections.emptyList())
            .rateSearchId(new RateSearchId("10", "100").toString())
            .ratePlanCount(100)
            .build();

        // when, then
        IllegalArgumentException exception = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> orderSearchService.search(searchRequest)
        );
    }

    @Test
    @DisplayName("주문하려는 상품이 검색되지 않으면 empty response 를 반환한다.")
    void empty() {
        // given
        SearchRequest searchRequest = SearchRequest.builder()
            .countryCode(CountryCode.KR)
            .propertyIds(List.of("1"))
            .checkin(LocalDate.of(2023, 5, 10))
            .checkout(LocalDate.of(2023, 5, 12))
            .adultCount(2)
            .childCount(0)
            .childAges(Collections.emptyList())
            .rateSearchId(new RateSearchId("10", "100").toString())
            .ratePlanCount(100)
            .build();
        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = OmhRoomsAvailabilityResponse.builder()
            .rooms(List.of(OmhRoomAvailability.builder()
                               .roomTypeCode("10")
                               .ratePlanCode("101")
                               .build()))
            .build();
        given(omhRoomsAvailabilityAgent.getRoomsAvailability(any()))
            .willReturn(omhRoomsAvailabilityResponse);

        // when
        SearchResponse searchResponse = orderSearchService.search(searchRequest);

        // then
        assertThat(searchResponse.getProperties().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("주문하려는 상품 재고가 있다면 order row 를 생성하고 API 로그를 저장한다.")
    void success() {
        // given
        SearchRequest searchRequest = SearchRequest.builder()
            .countryCode(CountryCode.KR)
            .propertyIds(List.of("1"))
            .checkin(LocalDate.of(2023, 5, 10))
            .checkout(LocalDate.of(2023, 5, 12))
            .adultCount(2)
            .childCount(0)
            .childAges(Collections.emptyList())
            .rateSearchId(new RateSearchId("10", "100").toString())
            .ratePlanCount(100)
            .build();

        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = OmhRoomsAvailabilityResponse.builder()
            .rooms(List.of(OmhRoomAvailability.builder()
                               .roomTypeCode("10")
                               .ratePlanCode("100")
                               .mealBasisCode(MealBasisCode.A)
                               .promotionType(PromotionType.NONE)
                               .totalNetAmount(BigDecimal.valueOf(10000))
                               .bedGroups(List.of(OmhBedGroup.builder()
                                                      .beds(List.of(OmhBed.builder()
                                                                        .bedTypeCode(BedTypeCode.BDT01)
                                                                        .bedTypeName("Single")
                                                                        .bedTypeCount(3)
                                                                        .build()))
                                                      .build()))
                               .facilities(Collections.emptyList())
                               .cancellationPolicy(OmhCancelPolicy.builder().build())
                               .leftRooms(3)
                               .rateType(RateType.PACKAGE_RATE)
                               .build()))
            .build();
        given(omhRoomsAvailabilityAgent.getRoomsAvailability(any()))
            .willReturn(omhRoomsAvailabilityResponse);

        given(commissionRateService.getMrtCommissionRate())
            .willReturn(BigDecimal.valueOf(20));

        given(orderConverter.toOrder(any(), any(), any()))
            .willReturn(Order.builder().build());

        given(orderProvider.upsert(any()))
            .willReturn(Order.builder().orderId(999L).build());

        // when
        SearchResponse searchResponse = orderSearchService.search(searchRequest);

        // then
        verify(orderProvider, times(1)).upsert(any());
        verify(reservationApiLogService, times(1)).upsertRoomsAvailabilityLog(eq(999L), eq(ApiLogType.REQUEST), anyString());
        verify(reservationApiLogService, times(1)).upsertRoomsAvailabilityLog(eq(999L), eq(ApiLogType.RESPONSE), anyString());

        assertThat(searchResponse.getProperties().get(0).getPropertyId()).isEqualTo("1");
        assertThat(searchResponse.getProperties().get(0).getRooms().get(0).getRoomId()).isEqualTo("10");
        assertThat(searchResponse.getProperties().get(0).getRooms().get(0).getRates().get(0).getRateId()).isEqualTo("100");
    }
}