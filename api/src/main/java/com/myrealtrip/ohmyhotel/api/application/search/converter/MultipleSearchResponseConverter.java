package com.myrealtrip.ohmyhotel.api.application.search.converter;

import com.myrealtrip.ohmyhotel.core.domain.search.dto.RatePlan;
import com.myrealtrip.ohmyhotel.core.service.CommonSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.service.RatePlanDistinctService;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse.OmhHotelAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse.OmhRoomSimpleAvailability;
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
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class MultipleSearchResponseConverter {

    private final RatePlanDistinctService ratePlanDistinctService;
    private final CommonSearchResponseConverter commonSearchResponseConverter;

    /**
     * 오마이호텔 재고/검색 응답을 통합숙소 재고/검색 응답으로 변환한다. (다건 호텔 검색시 사용)
     */
    public SearchResponse toSearchResponse(List<OmhHotelAvailability> omhHotelAvailabilities,
                                           BigDecimal mrtCommissionRate,
                                           int ratePlanCount,
                                           Map<Long, ZeroMargin> hotelIdToZeroMargin) {
        return SearchResponse.builder()
            .searchId(ProviderCode.OH_MY_HOTEL.name())
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .properties(toPropertyAvailabilities(omhHotelAvailabilities, mrtCommissionRate, ratePlanCount, hotelIdToZeroMargin))
            .build();
    }

    private List<PropertyAvailability> toPropertyAvailabilities(List<OmhHotelAvailability> omhHotelAvailabilities,
                                                                BigDecimal mrtCommissionRate,
                                                                int ratePlanCount,
                                                                Map<Long, ZeroMargin> hotelIdToZeroMargin) {
        if (CollectionUtils.isEmpty(omhHotelAvailabilities)) {
            return Collections.emptyList();
        }
        return omhHotelAvailabilities.stream()
            .map(omhHotelAvailability -> toPropertyAvailability(
                omhHotelAvailability,
                mrtCommissionRate,
                ratePlanCount,
                hotelIdToZeroMargin.getOrDefault(omhHotelAvailability.getHotelCode(), ZeroMargin.empty()))
            )
            .filter(propertyAvailability -> CollectionUtils.isNotEmpty(propertyAvailability.getRooms()))
            .collect(Collectors.toList());
    }

    private PropertyAvailability toPropertyAvailability(OmhHotelAvailability omhHotelAvailability,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount,
                                                        ZeroMargin zeroMargin) {
        return PropertyAvailability.builder()
            .propertyId(String.valueOf(omhHotelAvailability.getHotelCode()))
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .score(null)
            .rooms(toRoomAvailabilities(omhHotelAvailability.getRoomsGroupByRoomTypeCode(), mrtCommissionRate, ratePlanCount, zeroMargin))
            .build();
    }

    private List<RoomAvailability> toRoomAvailabilities(Map<String, List<OmhRoomSimpleAvailability>> roomsGroupByRoomTypeCode,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount,
                                                        ZeroMargin zeroMargin) {
        if (MapUtils.isEmpty(roomsGroupByRoomTypeCode)) {
            return Collections.emptyList();
        }
        return roomsGroupByRoomTypeCode.keySet().stream()
            .map(roomTypeCode -> toRoomAvailability(roomsGroupByRoomTypeCode.get(roomTypeCode), mrtCommissionRate, ratePlanCount, zeroMargin))
            .filter(roomAvailability -> CollectionUtils.isNotEmpty(roomAvailability.getRates()))
            .sorted(CommonSearchResponseConverter.ROOM_COMPARATOR)
            .collect(Collectors.toList());
    }

    private RoomAvailability toRoomAvailability(List<OmhRoomSimpleAvailability> omhRoomSimpleAvailabilities,
                                                BigDecimal mrtCommissionRate,
                                                int ratePlanCount,
                                                ZeroMargin zeroMargin) {
        return RoomAvailability.builder()
            .roomId(omhRoomSimpleAvailabilities.get(0).getRoomTypeCode())
            .roomName(commonSearchResponseConverter.toUnionStayRoomName(omhRoomSimpleAvailabilities.get(0).getRoomTypeName(), omhRoomSimpleAvailabilities.get(0).getRoomTypeNameByLanguage()))
            .rates(toRateAvailabilities(omhRoomSimpleAvailabilities, mrtCommissionRate, ratePlanCount, zeroMargin))
            .build();
    }

    private List<RateAvailability> toRateAvailabilities(List<OmhRoomSimpleAvailability> omhRoomSimpleAvailabilities,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount,
                                                        ZeroMargin zeroMargin) {
        if (CollectionUtils.isEmpty(omhRoomSimpleAvailabilities)) {
            return Collections.emptyList();
        }
        List<RatePlan> ratePlans = omhRoomSimpleAvailabilities.stream()
            .map(omhRoomSimpleAvailability -> RatePlan.builder()
                .ratePlanCode(omhRoomSimpleAvailability.getRatePlanCode())
                .ratePlanName(omhRoomSimpleAvailability.getRatePlanName())
                .cancelPolicy(omhRoomSimpleAvailability.getCancellationPolicy())
                .mealBasisCode(EnumUtils.getEnum(MealBasisCode.class, omhRoomSimpleAvailability.getMealBasisCode(), MealBasisCode.NONE))
                .totalNetAmount(omhRoomSimpleAvailability.getTotalNetAmount())
                .build())
            .collect(Collectors.toList());

        List<String> exposeRatePlanCode = ratePlanDistinctService.getExposeRatePlanCode(ratePlans);
        return omhRoomSimpleAvailabilities.stream()
            .filter(omhRoomSimpleAvailability -> isNull(omhRoomSimpleAvailability.getTotalMspAmount()) &&
                                                 exposeRatePlanCode.contains(omhRoomSimpleAvailability.getRatePlanCode()))
            .map(omhRoomSimpleAvailability -> toRateAvailability(omhRoomSimpleAvailability, mrtCommissionRate, zeroMargin))
            .sorted(CommonSearchResponseConverter.RATE_COMPARATOR)
            .limit(ratePlanCount)
            .collect(Collectors.toList());
    }

    private RateAvailability toRateAvailability(OmhRoomSimpleAvailability omhRoomSimpleAvailability, BigDecimal mrtCommissionRate, ZeroMargin zeroMargin) {
        return RateAvailability.builder()
            .rateId(omhRoomSimpleAvailability.getRatePlanCode())
            .optionId(null)
            .optionName(commonSearchResponseConverter.toUnionStayOptionName(omhRoomSimpleAvailability.getRoomTypeName(), omhRoomSimpleAvailability.getRoomTypeNameByLanguage(), omhRoomSimpleAvailability.getRatePlanName(), omhRoomSimpleAvailability.getRatePlanNameByLanguage()))
            .remainingRooms(omhRoomSimpleAvailability.getLeftRooms())
            .saleScenario(null)
            .benefits(commonSearchResponseConverter.toRateBenefits(omhRoomSimpleAvailability.getMealBasisCode()))
            .beds(toBeds(omhRoomSimpleAvailability))
            .cancelPolicies(commonSearchResponseConverter.toCancelPolicies(omhRoomSimpleAvailability.getCancellationPolicy(), mrtCommissionRate))
            .totalPayment(zeroMargin.isOn() ?
                          commonSearchResponseConverter.toZeroMarginTotalPayment(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin) :
                          commonSearchResponseConverter.toTotalPayment(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate))
            .commissions(commonSearchResponseConverter.toCommissions(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate))
            .surCharges(commonSearchResponseConverter.toSurcharges(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin))
            .onSiteSurCharges(Collections.emptyList())
            .monetaryPromotions(Collections.emptyList())
            .mrtPromotions(zeroMargin.isOn() ?
                           commonSearchResponseConverter.toZeroMarginMrtPromotions(omhRoomSimpleAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin) :
                           Collections.emptyList())
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
