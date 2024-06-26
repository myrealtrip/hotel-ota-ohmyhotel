package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCommonResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhRoomOccupancy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static java.util.Objects.isNull;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhRoomInfoResponse extends OmhCommonResponse {

    private Long hotelCode;

    private String roomTypeCode;

    private String roomTypeName;

    private String roomTypeNameByLanguage;

    private String roomTypeDescription;

    private String roomTypeDescriptionByLanguage;

    private Double roomSizeMeter;

    private Double roomSizeFeet;

    private List<OmhBedGroup> bedGroups;

    private OmhRoomOccupancy occupancy;

    private List<OmhRoomFacility> facilities;

    private List<OmhRoomPhoto> photos;

    @SuperBuilder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OmhRoomPhoto {

        private String url;

        private Integer order;

        private String caption;

        private String captionByLanguage;
    }

    public boolean isEmpty() {
        return isNull(hotelCode);
    }
}
