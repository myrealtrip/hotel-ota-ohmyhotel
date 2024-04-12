package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Facility;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelDescriptions;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class OmhHotelInfoMapper {

    private static final Long TEST_HOTEL_ID = 862813L;

    @Value("${spring.profiles.active}")
    private String profile;

    public Hotel toHotel(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
        if (!profile.contains("stage") &&
            !profile.contains("prod") &&
            omhHotelInfoAggr.getHotelCode().equals(TEST_HOTEL_ID)) {
            toTestHotel(omhHotelInfoAggr, hotelModifyInfo);
        }
        return toHotelBuilder(omhHotelInfoAggr, hotelModifyInfo).build();
    }

    /**
     * 통합숙소 입점을 위해 필요한 필수정보를 테스트 호텔이 가지고 있지 않아 임의로 정보를 채웁니다.
     */
    private Hotel toTestHotel(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
        return toHotelBuilder(omhHotelInfoAggr, hotelModifyInfo)
            .koAddress("2-19-2 Chiyozaki, Nishi-ku")
            .enAddress("2-19-2 Chiyozaki, Nishi-ku")
            .checkInTime("3:00 PM")
            .checkOutTime("11:00 AM")
            .latitude(34.67234)
            .longitude(135.479768)
            .build();
    }

    private Hotel.HotelBuilder toHotelBuilder(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
        OmhHotelInfo koInfo = omhHotelInfoAggr.getKoInfo();
        OmhHotelInfo enInfo = omhHotelInfoAggr.getEnInfo();

        return Hotel.builder()
            .hotelId(koInfo.getHotelCode())
            .koName(koInfo.getHotelName())
            .enName(enInfo.getHotelName())
            .status(koInfo.getStatus())
            .regionCode(koInfo.getRegionCode())
            .regionName(koInfo.getRegionName())
            .countryCode(koInfo.getCountryCode())
            .countryName(koInfo.getCountryName())
            .hotelType(koInfo.getHotelType())
            .starRating(koInfo.getStarRating())
            .floorCount(koInfo.getFloorCount())
            .roomCount(koInfo.getRoomCount())
            .phoneNo(koInfo.getPhoneNo())
            .faxNo(koInfo.getFaxNo())
            .zipCode(koInfo.getZipCode())
            .koAddress(koInfo.getAddress())
            .enAddress(enInfo.getAddress())
            .homepageUrl(koInfo.getHomepageUrl())
            .checkInTime(koInfo.getCheckInTime())
            .checkOutTime(koInfo.getCheckOutTime())
            .establishedDate(koInfo.getEstablishedDate())
            .renovatedDate(enInfo.getRenovatedDate())
            .latitude(koInfo.getLatitude())
            .longitude(koInfo.getLongitude())
            .recommendYn(koInfo.getRecommendYn())
            .legacyHotelCode(koInfo.getLegacyHotelCode())
            .lastUpdateDateTime(koInfo.getLastUpdateDatetime())
            .koDescriptions(isNull(koInfo.getDescriptions()) ? null :
                            HotelDescriptions.builder()
                                .introduction(koInfo.getDescriptions().getIntroduction())
                                .getThere(koInfo.getDescriptions().getGetThere())
                                .hotelFacility(koInfo.getDescriptions().getHotelFacility())
                                .roomFacility(koInfo.getDescriptions().getRoomFacility())
                                .attractions(koInfo.getDescriptions().getAttractions())
                                .cautions(koInfo.getDescriptions().getCautions())
                                .specialDescription(koInfo.getDescriptions().getSpecialDescription())
                                .build())
            .enDescriptions(isNull(enInfo.getDescriptions()) ? null :
                            HotelDescriptions.builder()
                                .introduction(enInfo.getDescriptions().getIntroduction())
                                .getThere(enInfo.getDescriptions().getGetThere())
                                .hotelFacility(enInfo.getDescriptions().getHotelFacility())
                                .roomFacility(enInfo.getDescriptions().getRoomFacility())
                                .attractions(enInfo.getDescriptions().getAttractions())
                                .cautions(enInfo.getDescriptions().getCautions())
                                .specialDescription(enInfo.getDescriptions().getSpecialDescription())
                                .build())
            .facilities(koInfo.getFacilities().stream()
                            .map(omhHotelFacility -> new Facility(omhHotelFacility.getFacilityCode(), omhHotelFacility.getFacilityName()))
                            .collect(Collectors.toList()))
            .photos(koInfo.getPhotos().stream()
                        .map(omhHotelPhoto -> new Photo(omhHotelPhoto.getUrl(), omhHotelPhoto.getOrder(), omhHotelPhoto.getCaption()))
                        .collect(Collectors.toList()))
            .createdAt(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getCreatedAt())
            .createdBy(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getCreatedBy())
            .updatedAt(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getUpdatedAt())
            .updatedBy(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getUpdatedBy())
            .deletedAt(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getDeletedAt())
            .deletedBy(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getDeletedBy());
    }
}
