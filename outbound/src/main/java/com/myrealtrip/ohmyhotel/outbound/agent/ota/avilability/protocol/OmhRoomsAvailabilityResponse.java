package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
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
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhRoomsAvailabilityResponse extends OmhCommonResponse {

    private List<RoomAvailability> rooms;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomAvailability {

        private String roomTypeCode;

        private String roomTypeName;

        private String roomTypeNameByLanguage;

        private String roomTypeDescription;

        private String roomTypeDescriptionByLanguage;

        private MealBasisCode mealBasisCode;

        private String mealBasisName;

        private String freeBreakfastName;

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
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChildPolicy {

        private Integer infantAge;

        private Integer childFromAge;

        private Integer childToAge;

        private Boolean freeStay;

        private Integer minimumGuestAge;
    }
}
