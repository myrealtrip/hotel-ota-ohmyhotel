package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmhStaticBulkHotelListResponse extends OmhCommonResponse {

    private Integer hotelCount;
    private List<OmhBulkHotel> hotels;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OmhBulkHotel {
        private Long hotelCode;
        private String hotelName;
        private String status;
        private String starRating;
        private String hotelType;
        private String phoneNo;
        private String zipCode;
        private String address;
        private Double latitude;
        private Double longitude;
        private Boolean recommendYn;
        private String countryCode;
        private Long regionCode;
        private String regionName;
        private Boolean directContractYn;
        private String legacyHotelCode;
    }
}
