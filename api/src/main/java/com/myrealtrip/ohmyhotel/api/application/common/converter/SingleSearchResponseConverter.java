package com.myrealtrip.ohmyhotel.api.application.common.converter;

import com.myrealtrip.ohmyhotel.core.service.BedDescriptionConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.constants.AttributeConstants;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.service.CommonSearchResponseConverter;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Bed;
import com.myrealtrip.unionstay.dto.hotelota.search.response.PropertyAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RoomAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RoomBenefit;
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

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class SingleSearchResponseConverter {

    private final CommonSearchResponseConverter commonSearchResponseConverter;
    private final BedDescriptionConverter bedDescriptionConverter;

    public SearchResponse empty() {
        return SearchResponse.builder()
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .searchId(ProviderCode.OH_MY_HOTEL.name())
            .properties(Collections.emptyList())
            .build();
    }

    /**
     * 오마이호텔 재고/검색 응답을 통합숙소 재고/검색 응답으로 변환한다. (주문서 검색시 사용)
     */
    public SearchResponse toSearchResponse(Long hotelId,
                                           OmhRoomAvailability omhRoomAvailability,
                                           BigDecimal mrtCommissionRate,
                                           int ratePlanCount,
                                           ZeroMargin zeroMargin,
                                           String searchId) {
        PropertyAvailability propertyAvailability = PropertyAvailability.builder()
            .propertyId(String.valueOf(hotelId))
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .score(null)
            .rooms(toRoomAvailabilities(Map.of(omhRoomAvailability.getRoomTypeCode(), List.of(omhRoomAvailability)), mrtCommissionRate, ratePlanCount, zeroMargin))
            .build();

        return SearchResponse.builder()
            .searchId(searchId)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .properties(List.of(propertyAvailability))
            .build();
    }

    /**
     * 오마이호텔 재고/검색 응답을 통합숙소 재고/검색 응답으로 변환한다. (단건 호텔 검색시 사용)
     */
    public SearchResponse toSearchResponse(Long hotelId,
                                           OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                                           BigDecimal mrtCommissionRate,
                                           int ratePlanCount,
                                           ZeroMargin zeroMargin) {
        return SearchResponse.builder()
            .searchId(ProviderCode.OH_MY_HOTEL.name())
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .properties(toPropertyAvailabilities(hotelId, omhRoomsAvailabilityResponse, mrtCommissionRate, ratePlanCount, zeroMargin))
            .build();
    }

    private List<PropertyAvailability> toPropertyAvailabilities(Long hotelId,
                                                                OmhRoomsAvailabilityResponse omhHotelsAvailabilityResponse,
                                                                BigDecimal mrtCommissionRate,
                                                                int ratePlanCount,
                                                                ZeroMargin zeroMargin) {
        PropertyAvailability propertyAvailability = toPropertyAvailability(hotelId, omhHotelsAvailabilityResponse, mrtCommissionRate, ratePlanCount, zeroMargin);
        if (CollectionUtils.isEmpty(propertyAvailability.getRooms())) {
            return Collections.emptyList();
        }
        return List.of(propertyAvailability);
    }

    private PropertyAvailability toPropertyAvailability(Long hotelId,
                                                        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount,
                                                        ZeroMargin zeroMargin) {
        return PropertyAvailability.builder()
            .propertyId(String.valueOf(hotelId))
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .score(null)
            .rooms(toRoomAvailabilities(omhRoomsAvailabilityResponse.getRoomsGroupByRoomTypeCode(), mrtCommissionRate, ratePlanCount, zeroMargin))
            .build();
    }

    private List<RoomAvailability> toRoomAvailabilities(Map<String, List<OmhRoomAvailability>> roomsGroupByRoomTypeCode,
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

    private RoomAvailability toRoomAvailability(List<OmhRoomAvailability> omhRoomAvailabilities,
                                                BigDecimal mrtCommissionRate,
                                                int ratePlanCount,
                                                ZeroMargin zeroMargin) {
        return RoomAvailability.builder()
            .roomId(omhRoomAvailabilities.get(0).getRoomTypeCode())
            .roomName(commonSearchResponseConverter.toUnionStayRoomName(omhRoomAvailabilities.get(0).getRoomTypeName(), omhRoomAvailabilities.get(0).getRoomTypeNameByLanguage()))
            .rates(toRateAvailabilities(omhRoomAvailabilities, mrtCommissionRate, ratePlanCount, zeroMargin))
            .benefits(toRoomBenefit(omhRoomAvailabilities.get(0)))
            .build();
    }

    private List<RoomBenefit> toRoomBenefit(OmhRoomAvailability omhRoomAvailability) {
        if (CollectionUtils.isEmpty(omhRoomAvailability.getFacilities())) {
            return Collections.emptyList();
        }
        return omhRoomAvailability.getFacilities().stream()
            .map(omhRoomFacility -> RoomBenefit.builder()
                .id(omhRoomFacility.getFacilityCode())
                .group(AttributeConstants.ROOM_FACILITY_PROVIDER_ATTRIBUTE_GROUP)
                .benefitName(StringUtils.isNotBlank(omhRoomFacility.getFacilityNameByLanguage()) ?
                             omhRoomFacility.getFacilityNameByLanguage() :
                             omhRoomFacility.getFacilityName())
                .build())
            .collect(Collectors.toList());
    }

    private List<RateAvailability> toRateAvailabilities(List<OmhRoomAvailability> omhRoomAvailabilities,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount,
                                                        ZeroMargin zeroMargin) {
        if (CollectionUtils.isEmpty(omhRoomAvailabilities)) {
            return Collections.emptyList();
        }
        return omhRoomAvailabilities.stream()
            .filter(omhRoomAvailability -> isNull(omhRoomAvailability.getTotalMspAmount()))
            .map(omhRoomSimpleAvailability -> toRateAvailability(omhRoomSimpleAvailability, mrtCommissionRate, zeroMargin))
            .sorted(CommonSearchResponseConverter.RATE_COMPARATOR)
            .limit(ratePlanCount)
            .collect(Collectors.toList());
    }

    private RateAvailability toRateAvailability(OmhRoomAvailability omhRoomAvailability, BigDecimal mrtCommissionRate, ZeroMargin zeroMargin) {

        return RateAvailability.builder()
            .rateId(omhRoomAvailability.getRatePlanCode())
            .optionId(null)
            .optionName(commonSearchResponseConverter.toUnionStayOptionName(omhRoomAvailability.getRoomTypeName(), omhRoomAvailability.getRoomTypeNameByLanguage(), omhRoomAvailability.getRatePlanName(), omhRoomAvailability.getRatePlanNameByLanguage()))
            .remainingRooms(omhRoomAvailability.getLeftRooms())
            .saleScenario(null)
            .benefits(commonSearchResponseConverter.toRateBenefits(omhRoomAvailability.getMealBasisCode()))
            .beds(toBeds(omhRoomAvailability))
            .cancelPolicies(commonSearchResponseConverter.toCancelPolicies(omhRoomAvailability.getCancellationPolicy(), mrtCommissionRate))
            .totalPayment(zeroMargin.isOn() ?
                          commonSearchResponseConverter.toZeroMarginTotalPayment(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin) :
                          commonSearchResponseConverter.toTotalPayment(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate))
            .commissions(commonSearchResponseConverter.toCommissions(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate))
            .surCharges(commonSearchResponseConverter.toSurcharges(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin))
            .onSiteSurCharges(Collections.emptyList())
            .monetaryPromotions(Collections.emptyList())
            .mrtPromotions(zeroMargin.isOn() ?
                           commonSearchResponseConverter.toZeroMarginMrtPromotions(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate, zeroMargin) :
                           Collections.emptyList())
            .extraBeds(0)
            .representInclusions(Collections.emptyList())
            .optionDescriptions(Collections.emptyList())
            .minNights(commonSearchResponseConverter.toMinNights(omhRoomAvailability.getPromotionType(), omhRoomAvailability.getPromotionValue()))
            .maxNights(null)
            .timeLimitedImages(Collections.emptyList())
            .recommendOption(null)
            .applyOptionStartDate(null)
            .applyOptionEndDate(null)
            .build();
    }

    private List<Bed> toBeds(OmhRoomAvailability omhRoomAvailability) {
        Bed bed = Bed.builder()
            .id(new RateSearchId(omhRoomAvailability.getRoomTypeCode(), omhRoomAvailability.getRatePlanCode()).toString())
            .token(omhRoomAvailability.getRatePlanCode())
            .description(bedDescriptionConverter.toUnionStayBedDescription(omhRoomAvailability.getBedGroups()))
            .extraBeds(0)
            .build();
        return List.of(bed);
    }
}
