package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse.OmhHotelAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse.OmhRoomSimpleAvailability;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Bed;
import com.myrealtrip.unionstay.dto.hotelota.search.response.PropertyAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RoomAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MultipleSearchResponseConverter {

    private final CommonSearchResponseConverter commonSearchResponseConverter;

    /**
     * 오마이호텔 재고/검색 응답을 통합숙소 재고/검색 응답으로 변환한다. (다건 검색시 사용)
     *
     * @param omhHotelsAvailabilityResponse 오마이호텔 다건 재고검색 API 응답
     * @param mrtCommissionRate             마리트 수수료율 (입금가 기준)
     * @return
     */
    public SearchResponse toSearchResponse(OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse,
                                           BigDecimal mrtCommissionRate,
                                           int ratePlanCount) {
        return SearchResponse.builder()
            .searchId(null)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .properties(toPropertyAvailabilities(omhHotelsAvailabilityResponse, mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<PropertyAvailability> toPropertyAvailabilities(OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse,
                                                                BigDecimal mrtCommissionRate,
                                                                int ratePlanCount) {
        if (CollectionUtils.isEmpty(omhHotelsAvailabilityResponse.getHotels())) {
            return Collections.emptyList();
        }
        return omhHotelsAvailabilityResponse.getHotels().stream()
            .map(omhHotelAvailability -> toPropertyAvailability(omhHotelAvailability, mrtCommissionRate, ratePlanCount))
            .filter(propertyAvailability -> CollectionUtils.isNotEmpty(propertyAvailability.getRooms()))
            .collect(Collectors.toList());
    }

    private PropertyAvailability toPropertyAvailability(OmhHotelAvailability omhHotelAvailability,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount) {
        return PropertyAvailability.builder()
            .propertyId(String.valueOf(omhHotelAvailability.getHotelCode()))
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .score(null)
            .rooms(toRoomAvailabilities(omhHotelAvailability.getRoomsGroupByRoomTypeCode(), mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<RoomAvailability> toRoomAvailabilities(Map<String, List<OmhRoomSimpleAvailability>> roomsGroupByRoomTypeCode,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount) {
        if (MapUtils.isEmpty(roomsGroupByRoomTypeCode)) {
            return Collections.emptyList();
        }
        return roomsGroupByRoomTypeCode.keySet().stream()
            .map(roomTypeCode -> toRoomAvailability(roomsGroupByRoomTypeCode.get(roomTypeCode), mrtCommissionRate, ratePlanCount))
            .filter(roomAvailability -> CollectionUtils.isNotEmpty(roomAvailability.getRates()))
            .sorted(CommonSearchResponseConverter.ROOM_COMPARATOR)
            .collect(Collectors.toList());
    }

    private RoomAvailability toRoomAvailability(List<OmhRoomSimpleAvailability> omhRoomSimpleAvailabilities,
                                                BigDecimal mrtCommissionRate,
                                                int ratePlanCount) {
        return RoomAvailability.builder()
            .roomId(omhRoomSimpleAvailabilities.get(0).getRoomTypeCode())
            .roomName(StringUtils.isNotBlank(omhRoomSimpleAvailabilities.get(0).getRoomTypeNameByLanguage()) ?
                      omhRoomSimpleAvailabilities.get(0).getRoomTypeNameByLanguage() :
                      omhRoomSimpleAvailabilities.get(0).getRoomTypeName())
            .rates(toRateAvailabilities(omhRoomSimpleAvailabilities, mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<RateAvailability> toRateAvailabilities(List<OmhRoomSimpleAvailability> omhRoomSimpleAvailabilities,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount) {
        if (CollectionUtils.isEmpty(omhRoomSimpleAvailabilities)) {
            return Collections.emptyList();
        }
        return omhRoomSimpleAvailabilities.stream()
            .map(omhRoomSimpleAvailability -> toRateAvailability(omhRoomSimpleAvailability, mrtCommissionRate))
            .sorted(CommonSearchResponseConverter.RATE_COMPARATOR)
            .limit(ratePlanCount)
            .collect(Collectors.toList());
    }

    private RateAvailability toRateAvailability(OmhRoomSimpleAvailability omhRoomSimpleAvailability, BigDecimal mrtCommissionRate) {
        return RateAvailability.builder()
            .rateId(omhRoomSimpleAvailability.getRatePlanCode())
            .optionId(null)
            .optionName(StringUtils.isNotBlank(omhRoomSimpleAvailability.getRatePlanNameByLanguage()) ?
                        omhRoomSimpleAvailability.getRatePlanNameByLanguage() :
                        omhRoomSimpleAvailability.getRatePlanName())
            .remainingRooms(omhRoomSimpleAvailability.getLeftRooms())
            .saleScenario(null)
            .benefits(commonSearchResponseConverter.toRateBenefits(omhRoomSimpleAvailability.getMealBasisCode()))
            .beds(toBeds(omhRoomSimpleAvailability))
            .cancelPolicies(commonSearchResponseConverter.toCancelPolicies(omhRoomSimpleAvailability.getCancellationPolicy()))
            .totalPayment(commonSearchResponseConverter.toTotalPayment(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate))
            .commissions(commonSearchResponseConverter.toCommissions(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate))
            .surCharges(Collections.emptyList())
            .onSiteSurCharges(Collections.emptyList())
            .monetaryPromotions(Collections.emptyList())
            .mrtPromotions(Collections.emptyList())
            .extraBeds(0)
            .representInclusions(Collections.emptyList())
            .optionDescriptions(Collections.emptyList())
            .minNights(commonSearchResponseConverter.toMinNights(omhRoomSimpleAvailability.getPromotionType(), omhRoomSimpleAvailability.getPromotionValue()))
            .maxNights(null)
            .timeLimitedImages(Collections.emptyList())
            .recommendOption(null)
            .applyOptionStartDate(null)
            .applyOptionEndDate(null)
            .build();
    }

    private List<Bed> toBeds(OmhRoomSimpleAvailability omhRoomSimpleAvailability) {
        Bed bed = Bed.builder()
            .id(new RateSearchId(omhRoomSimpleAvailability.getRoomTypeCode(), omhRoomSimpleAvailability.getRatePlanCode()).toString())
            .token(omhRoomSimpleAvailability.getRatePlanCode())
            .description(null) // 다건 검색에서는 침대타입 제공 불가
            .extraBeds(0)
            .build();
        return List.of(bed);
    }
}
