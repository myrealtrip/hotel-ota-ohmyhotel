package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhFacility;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhStaticHotelInfoListResponse extends OmhCommonResponse {

    private List<OmhHotelInfo> hotels;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhHotelInfo {

        private Long hotelCode;

        private String hotelName;

        private HotelStatus status;

        private String starRating;

        private String hotelType;

        private String phoneNo;

        private String zipCode;

        private String address;

        private Double latitude;

        private Double longitude;

        private Boolean recommendYn;

        private String countryCode;

        private String countryName;

        private Long regionCode;

        private String regionName;

        private Boolean directContractYn;

        private String legacyHotelCode;

        private String floorCount;

        private String roomCount;

        private String faxNo;

        private String homepageUrl;

        private String checkInTime;

        private String checkOutTime;

        private String establishedDate; // YYYY-MM

        private String renovatedDate; // YYYY-MM

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUpdateDatetime;

        private OmhHotelDescriptions descriptions;

        private List<OmhFacility> facilities;

        private List<OmhHotelPhoto> photos;
    }

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhHotelDescriptions {
        private String introduction;

        private String getThere;

        private String hotelFacility;

        private String roomFacility;

        private String attractions;

        private String cautions;

        private String specialDescription;
    }


    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhHotelPhoto {

        private String url;

        private int order;

        private String caption;
    }
}
