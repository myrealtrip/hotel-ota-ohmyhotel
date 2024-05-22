package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.BookingRequestCode;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.PenaltyBasis;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.OmhRoomGuestDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhBookingDetailResponse extends OmhCommonResponse {

    private OmhBookingStatus status;

    private OmhBookingCodes bookingCodes;

    private OmhBookingDetailContactPerson contactPerson;

    private Long hotelCode;

    private String hotelName;

    private String hotelNameByLanguage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private Long regionCode;

    private String regionName;

    private String roomTypeCode;

    private String roomTypeName;

    private String roomTypeNameByLanguage;

    private String ratePlanCode;

    private MealBasisCode mealBasisCode;

    private String mealBasisName;

    private String freeBreakfastName;

    private List<OmhBookingDetailRoomGuestInfo> rooms;

    private OmhBookingDetailAmount amount;

    private OmhBookingDetailCancelPolicy cancellationPolicy;

    private List<OmhBookingDetailRequest> requests;

    private String payableBy;

    private String emergencyContact;



    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingCodes {

        private String channelBookingCode;

        private String ohMyBookingCode;

        private String hotelConfirmationNo;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingDetailContactPerson {

        private String name;

        private String email;

        private String countryPhoneCode;

        private String mobileNo;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingDetailRoomGuestInfo {

        private Integer roomNo;

        private String roomReferenceCode;

        private List<OmhRoomGuestDetail> guests;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingDetailAmount {

        private String currency;

        private BigDecimal totalNetAmount;

        private RateType rateType;

        private List<OmhNightlyAmount> nightly;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingDetailCancelPolicy {

        private Boolean isNonRefundable;

        private String timeZone;

        private PenaltyBasis penaltyBasis;

        private List<OmhBookingCancelPolicyValue> policies;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingCancelPolicyValue {

        @JsonFormat(pattern = "[yyyy-MM-dd HH:mm:ss][yyyy-MM-dd HH:mm]")
        private LocalDateTime fromDateTime;

        @JsonFormat(pattern = "[yyyy-MM-dd HH:mm:ss][yyyy-MM-dd HH:mm]")
        private LocalDateTime toDateTime;

        private BigDecimal penaltyAmount;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBookingDetailRequest {

        private BookingRequestCode code;

        private String name;

        private String lateCheckIn;

        private String comment;
    }
}
