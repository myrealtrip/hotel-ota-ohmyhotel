package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
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
public class SingleSearchResponseConverter {

    private final CommonSearchResponseConverter commonSearchResponseConverter;

    /**
     * 오마이호텔 재고/검색 응답을 통합숙소 재고/검색 응답으로 변환한다. (단건 검색시 사용)
     *
     * @param omhRoomsAvailabilityResponse 오마이호텔 단건 재고검색 API 응답
     * @param mrtCommissionRate            마리트 수수료율 (입금가 기준)
     * @return
     */
    public SearchResponse toSearchResponse(Long hotelId,
                                           OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                                           BigDecimal mrtCommissionRate,
                                           int ratePlanCount) {
        return SearchResponse.builder()
            .searchId(null)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .properties(toPropertyAvailabilities(hotelId, omhRoomsAvailabilityResponse, mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<PropertyAvailability> toPropertyAvailabilities(Long hotelId,
                                                                OmhRoomsAvailabilityResponse omhHotelsAvailabilityResponse,
                                                                BigDecimal mrtCommissionRate,
                                                                int ratePlanCount) {
        PropertyAvailability propertyAvailability = toPropertyAvailability(hotelId, omhHotelsAvailabilityResponse, mrtCommissionRate, ratePlanCount);
        if (CollectionUtils.isEmpty(propertyAvailability.getRooms())) {
            return Collections.emptyList();
        }
        return List.of(propertyAvailability);
    }

    private PropertyAvailability toPropertyAvailability(Long hotelId,
                                                        OmhRoomsAvailabilityResponse omhHotelsAvailabilityResponse,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount) {
        return PropertyAvailability.builder()
            .propertyId(String.valueOf(hotelId))
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .score(null)
            .rooms(toRoomAvailabilities(omhHotelsAvailabilityResponse.getRoomsGroupByRoomTypeCode(), mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<RoomAvailability> toRoomAvailabilities(Map<String, List<OmhRoomAvailability>> roomsGroupByRoomTypeCode,
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

    private RoomAvailability toRoomAvailability(List<OmhRoomAvailability> omhRoomAvailabilities,
                                                BigDecimal mrtCommissionRate,
                                                int ratePlanCount) {
        return RoomAvailability.builder()
            .roomId(omhRoomAvailabilities.get(0).getRoomTypeCode())
            .roomName(StringUtils.isNotBlank(omhRoomAvailabilities.get(0).getRoomTypeNameByLanguage()) ?
                      omhRoomAvailabilities.get(0).getRoomTypeNameByLanguage() :
                      omhRoomAvailabilities.get(0).getRoomTypeName())
            .rates(toRateAvailabilities(omhRoomAvailabilities, mrtCommissionRate, ratePlanCount))
            .build();
    }

    private List<RateAvailability> toRateAvailabilities(List<OmhRoomAvailability> omhSimpleAvailabilities,
                                                        BigDecimal mrtCommissionRate,
                                                        int ratePlanCount) {
        if (CollectionUtils.isEmpty(omhSimpleAvailabilities)) {
            return Collections.emptyList();
        }
        return omhSimpleAvailabilities.stream()
            .map(omhRoomSimpleAvailability -> toRateAvailability(omhRoomSimpleAvailability, mrtCommissionRate))
            .sorted(CommonSearchResponseConverter.RATE_COMPARATOR)
            .limit(ratePlanCount)
            .collect(Collectors.toList());
    }

    private RateAvailability toRateAvailability(OmhRoomAvailability omhRoomAvailability, BigDecimal mrtCommissionRate) {
        return RateAvailability.builder()
            .rateId(omhRoomAvailability.getRatePlanCode())
            .optionId(null)
            .optionName(StringUtils.isNotBlank(omhRoomAvailability.getRatePlanNameByLanguage()) ?
                        omhRoomAvailability.getRatePlanNameByLanguage() :
                        omhRoomAvailability.getRatePlanName())
            .remainingRooms(omhRoomAvailability.getLeftRooms())
            .saleScenario(null)
            .benefits(commonSearchResponseConverter.toRateBenefits(omhRoomAvailability.getMealBasisCode()))
            .beds(toBeds(omhRoomAvailability))
            .cancelPolicies(commonSearchResponseConverter.toCancelPolicies(omhRoomAvailability.getCancellationPolicy()))
            .totalPayment(commonSearchResponseConverter.toTotalPayment(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate))
            .commissions(commonSearchResponseConverter.toCommissions(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate))
            .surCharges(Collections.emptyList())
            .onSiteSurCharges(Collections.emptyList())
            .monetaryPromotions(Collections.emptyList())
            .mrtPromotions(Collections.emptyList())
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
        String unionStayBedDescription = omhRoomAvailability.getBedGroups().stream()
            .map(this::toBedGroupDescription)
            .collect(Collectors.joining("또는 "));

        Bed bed = Bed.builder()
            .id(new RateSearchId(omhRoomAvailability.getRoomTypeCode(), omhRoomAvailability.getRatePlanCode()).toString())
            .token(omhRoomAvailability.getRatePlanCode())
            .description(unionStayBedDescription)
            .extraBeds(0)
            .build();
        return List.of(bed);
    }

    private String toBedGroupDescription(OmhBedGroup omhBedGroup) {
        return omhBedGroup.getBeds().stream()
            .map(bed -> bed.getBedTypeName() + " " + bed.getBedTypeCode() + "개") // TODO bedTypeName 정책 필요
            .collect(Collectors.joining(","));
    }
}
