package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Facility;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelDescriptions;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class OmhHotelInfoMapper {

    private static final List<Long> DEV_TEST_HOTEL_IDS = List.of(862813L);
    private static final List<Long> PROD_TEST_HOTEL_IDS = List.of(858846L, 732447L, 164418L, 655329L, 985004L, 116929L, 370496L);

    private static final Map<String, List<Long>> TEST_HOTEL_IDS_MAP = Map.of(
        "local", DEV_TEST_HOTEL_IDS,
        "dev", DEV_TEST_HOTEL_IDS,
        "dev01", DEV_TEST_HOTEL_IDS,
        "dev02", DEV_TEST_HOTEL_IDS,
        "test", DEV_TEST_HOTEL_IDS,
        "test01", DEV_TEST_HOTEL_IDS,
        "test02", DEV_TEST_HOTEL_IDS,
        "stage", PROD_TEST_HOTEL_IDS,
        "prod", PROD_TEST_HOTEL_IDS
    );

    @Value("${spring.profiles.active}")
    private String profile;

    public Hotel toHotel(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
        if (TEST_HOTEL_IDS_MAP.get(profile).contains(omhHotelInfoAggr.getHotelCode())) {
            return toTestHotel(omhHotelInfoAggr, hotelModifyInfo);
        }
        return toHotelBuilder(omhHotelInfoAggr, hotelModifyInfo).build();
    }

    /**
     * 통합숙소 입점을 위해 필요한 필수정보를 테스트 호텔이 가지고 있지 않을경우 임의로 정보를 채웁니다.
     */
    private Hotel toTestHotel(OmhHotelInfoAggr omhHotelInfoAggr, HotelModifyInfo hotelModifyInfo) {
        return toHotelBuilder(omhHotelInfoAggr, hotelModifyInfo)
            .koAddress(StringUtils.isEmpty(omhHotelInfoAggr.getKoInfo().getAddress()) ?
                       "2-19-2 Chiyozaki, Nishi-ku" :
                       omhHotelInfoAggr.getKoInfo().getAddress())
            .enAddress(StringUtils.isEmpty(omhHotelInfoAggr.getEnInfo().getAddress()) ?
                       "2-19-2 Chiyozaki, Nishi-ku" :
                       omhHotelInfoAggr.getKoInfo().getAddress())
            .checkInTime(StringUtils.isEmpty(omhHotelInfoAggr.getKoInfo().getCheckInTime()) ?
                         "3:00 PM" :
                         omhHotelInfoAggr.getKoInfo().getCheckInTime())
            .checkOutTime(StringUtils.isEmpty(omhHotelInfoAggr.getKoInfo().getCheckOutTime()) ?
                          "11:00 AM" :
                          omhHotelInfoAggr.getKoInfo().getCheckOutTime())
            .latitude((isNull(omhHotelInfoAggr.getKoInfo().getLatitude()) || omhHotelInfoAggr.getKoInfo().getLatitude() == 0) ?
                      34.67234 :
                      omhHotelInfoAggr.getKoInfo().getLatitude())
            .longitude((isNull(omhHotelInfoAggr.getKoInfo().getLongitude()) || omhHotelInfoAggr.getKoInfo().getLongitude() == 0) ?
                       135.479768 :
                       omhHotelInfoAggr.getKoInfo().getLongitude())
            .photos(CollectionUtils.isEmpty(omhHotelInfoAggr.getKoInfo().getPhotos()) ?
                    List.of(Photo.builder()
                                .url("https://photos01.ohmyhotel.com/hotels/11000000/10010000/10007500/10007435/039152d8_z.jpg")
                                .order(1)
                                .build()) :
                    omhHotelInfoAggr.getKoInfo().getPhotos().stream()
                        .map(omhHotelPhoto -> new Photo(omhHotelPhoto.getUrl(), omhHotelPhoto.getOrder(), omhHotelPhoto.getCaption()))
                        .collect(Collectors.toList())
            )
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
