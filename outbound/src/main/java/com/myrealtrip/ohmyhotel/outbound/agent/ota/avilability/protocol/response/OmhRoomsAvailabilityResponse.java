package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhRoomOccupancy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhRoomsAvailabilityResponse extends OmhCommonResponse {

    private List<OmhRoomAvailability> rooms;

    @JsonIgnore
    public Map<String, List<OmhRoomAvailability>> getRoomsGroupByRoomTypeCode() {
        if (isNull(rooms)) {
            return Collections.emptyMap();
        }
        return rooms.stream()
            .collect(Collectors.groupingBy(OmhRoomAvailability::getRoomTypeCode));
    }

    @SuperBuilder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OmhRoomAvailability {

        private String roomTypeCode;

        private String roomTypeName;

        private String roomTypeNameByLanguage;

        private String roomTypeDescription;

        private String roomTypeDescriptionByLanguage;

        private String mealBasisCode;

        private String mealBasisName;

        private String freeBreakfastName;

        private String roomToken;

        private String ratePlanCode;

        private String ratePlanName;

        private String ratePlanNameByLanguage;

        private PromotionType promotionType;

        /**
         * promotion value according to promotion type, if any.
         * Early_Bird - {value} Days in Advance
         * Last_Minute - within {value} days until check-in
         * Min_Nights - minimum {value} nights
         * Nth_Nights_Free - {value}th night free
         * Hotel_Package - no value
         * None - no value
         */
        private Integer promotionValue;

        private String promotionInfo;

        private String promotionInfoByLanguage;

        private Double roomSizeMeter;

        private Double roomSizeFeet;

        private RateType rateType;

        private Integer leftRooms;

        private String currency;

        private BigDecimal totalNetAmount;

        private BigDecimal totalMspAmount;

        private List<OmhNightlyAmount> nightly;

        private OmhCancelPolicy cancellationPolicy;

        private OmhRoomOccupancy occupancy;

        private List<OmhRoomFacility> facilities;

        private List<OmhBedGroup> bedGroups;

        private ChildPolicy childPolicy;

        @JsonIgnore
        public void changeTotalNetAmount(BigDecimal totalNetAmount) {
            this.totalNetAmount = totalNetAmount;
        }
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChildPolicy {

        private Integer infantAge;

        private Integer childFromAge;

        private Integer childToAge;

        private Boolean freeStay;

        private Integer minimumGuestAge;
    }
}
