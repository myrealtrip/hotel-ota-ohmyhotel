package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancellationPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
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
public class OmhHotelsAvailabilityResponse extends OmhCommonResponse {

    private List<HotelAvailability> hotels;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HotelAvailability {

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

        private List<RoomSimpleAvailability> rooms;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RoomSimpleAvailability {

        private String roomTypeCode;

        private String roomTypeName;

        private String roomTypeNameByLanguage;

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

        private RateType rateType;

        private Integer leftRooms;

        private String currency;

        private BigDecimal totalNetAmount;

        private BigDecimal totalMspAmount;

        private List<OmhNightlyAmount> nightly;

        private OmhCancellationPolicy cancellationPolicy;
    }

}
