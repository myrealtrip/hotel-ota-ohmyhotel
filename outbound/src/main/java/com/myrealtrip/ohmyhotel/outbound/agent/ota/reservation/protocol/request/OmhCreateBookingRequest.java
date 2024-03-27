package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.BookingRequestCode;
import com.myrealtrip.ohmyhotel.enumarate.Gender;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OmhCreateBookingRequest {

    @Builder.Default
    private String nationalityCode = "KR";

    private Language language;

    private String channelBookingCode;

    private OmhCreateBookingContactPerson contactPerson;

    private Long hotelCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private String roomTypeCode;

    private String ratePlanCode;

    private String freeBreakfastName;

    private List<OmhRoomGuestInfo> rooms;

    private List<OmhBookingRequest> requests;

    private RateType rateType;

    private BigDecimal totalNetAmount;

    @Builder
    @Getter
    public static class OmhCreateBookingContactPerson {

        private String name;

        private String email;

        @Builder.Default
        private String countryPhoneCode = "+82";

        private String mobileNo;
    }

    @Builder
    @Getter
    public static class OmhRoomGuestInfo {

        private Integer roomNo;

        private List<OmhRoomGuestDetail> guests;
    }

    @Builder
    @Getter
    public static class OmhRoomGuestDetail {

        private String lastName;

        private String firstName;

        private Gender gender;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;
    }

    @Builder
    @Getter
    public static class OmhBookingRequest {

        private BookingRequestCode code;

        private String lateCheckIn;

        private String comment;
    }
}
