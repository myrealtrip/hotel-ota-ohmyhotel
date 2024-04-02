package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Facility;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelDescriptions;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Mapper(componentModel = "spring")
public interface OmhHotelInfoMapper {

    default Hotel toHotel(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
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
            .koDescriptions(HotelDescriptions.builder()
                                .introduction(koInfo.getDescriptions().getIntroduction())
                                .getThere(koInfo.getDescriptions().getGetThere())
                                .hotelFacility(koInfo.getDescriptions().getHotelFacility())
                                .roomFacility(koInfo.getDescriptions().getRoomFacility())
                                .attractions(koInfo.getDescriptions().getAttractions())
                                .cautions(koInfo.getDescriptions().getCautions())
                                .specialDescription(koInfo.getDescriptions().getSpecialDescription())
                                .build())
            .enDescriptions(HotelDescriptions.builder()
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
            .deletedBy(isNull(hotelModifyInfo) ? null : hotelModifyInfo.getDeletedBy())
            .build();
    }
}
