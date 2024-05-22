package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhHotelsAvailabilityResponse extends OmhCommonResponse {

    private List<OmhHotelAvailability> hotels;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhHotelAvailability {

        private Long hotelCode;

        private String hotelName;

        private String hotelNameByLanguage;

        private String hotelType;

        private String starRating;

        private String zipCode;

        private String address;

        private String addressByLanguage;

        private Double latitude;

        private Double longitude;

        private Boolean recommendYn;

        private List<OmhRoomSimpleAvailability> rooms;

        @JsonIgnore
        public Map<String, List<OmhRoomSimpleAvailability>> getRoomsGroupByRoomTypeCode() {
            if (isNull(rooms)) {
                return Collections.emptyMap();
            }
            return rooms.stream()
                .collect(Collectors.groupingBy(OmhRoomSimpleAvailability::getRoomTypeCode));
        }
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhRoomSimpleAvailability {

        private String roomTypeCode;

        private String roomTypeName;

        private String roomTypeNameByLanguage;

        private String mealBasisCode;

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

        private RateType rateType;

        private Integer leftRooms;

        private String currency;

        private BigDecimal totalNetAmount;

        private BigDecimal totalMspAmount;

        private List<OmhNightlyAmount> nightly;

        private OmhCancelPolicy cancellationPolicy;
    }

}
