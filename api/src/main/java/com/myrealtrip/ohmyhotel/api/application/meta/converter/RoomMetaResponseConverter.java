package com.myrealtrip.ohmyhotel.api.application.meta.converter;

import com.myrealtrip.ohmyhotel.api.application.common.BedDescriptionConverter;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhBedGroup;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomFacility;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomInfoResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomInfoResponse.OmhRoomPhoto;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.constant.RoomStatus;
import com.myrealtrip.unionstay.common.constant.RoomVisualCategory;
import com.myrealtrip.unionstay.common.constant.VisualType;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage.Image;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaAttribute;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaBed;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaBed.BedConfiguration;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaGuestPolicy;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaImage;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class RoomMetaResponseConverter {

    private static final String FACILITY_PROVIDER_ATTRIBUTE_GROUP = "FACILITY";
    private static final String ROOM_SIZE_PROVIDER_ATTRIBUTE_GROUP = "AMENITIES_ROOMS";
    private static final String ROOM_SIZE_METER_PROVIDER_ATTRIBUTE_ID = "cid_square_meters";
    private static final String ROOM_SIZE_FEET_PROVIDER_ATTRIBUTE_ID = "cid_square_feet";
    private static final String ROOM_SIZE_METER_PROVIDER_LABEL_FORMAT = "객실 크기(㎡) - %s";
    private static final String ROOM_SIZE_FEET_PROVIDER_LABEL_FORMAT = "객실 크기(제곱피트) - %s";

    private final BedDescriptionConverter bedDescriptionConverter;

    public RoomMetaResponse toRoomMetaResponse(OmhRoomInfoResponse omhRoomInfoResponse) {
        return RoomMetaResponse.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .providerPropertyId(String.valueOf(omhRoomInfoResponse.getHotelCode()))
            .providerRoomId(omhRoomInfoResponse.getRoomTypeCode())
            .roomCount(null)
            .koName(omhRoomInfoResponse.getRoomTypeNameByLanguage())
            .enName(omhRoomInfoResponse.getRoomTypeName())
            .koDescription(omhRoomInfoResponse.getRoomTypeDescriptionByLanguage())
            .enDescription(omhRoomInfoResponse.getRoomTypeDescription())
            .guestPolicy(RoomMetaGuestPolicy.builder().build()) // 오마이호텔 GuestPolicy 정보는 ratePlanCode 에 따라 달라지므로 객실레벨 정보에는 넣지 않는다.
            .attributes(toRoomMetaAttributes(omhRoomInfoResponse))
            .images(toRoomMetaImages(omhRoomInfoResponse))
            .beds(toBeds(omhRoomInfoResponse))
            .roomStatus(RoomStatus.ENABLED)
            .build();
    }

    private List<RoomMetaAttribute> toRoomMetaAttributes(OmhRoomInfoResponse omhRoomInfoResponse) {
        List<RoomMetaAttribute> roomMetaAttributes = new ArrayList<>();
        for (OmhRoomFacility omhRoomFacility : omhRoomInfoResponse.getFacilities()) {
            RoomMetaAttribute roomMetaAttribute = RoomMetaAttribute.builder()
                .providerAttributeGroup(FACILITY_PROVIDER_ATTRIBUTE_GROUP)
                .providerAttributeId(omhRoomFacility.getFacilityCode())
                .providerLabel(StringUtils.isNotBlank((omhRoomFacility.getFacilityNameByLanguage())) ?
                               omhRoomFacility.getFacilityNameByLanguage() :
                               omhRoomFacility.getFacilityName())
                .build();
            roomMetaAttributes.add(roomMetaAttribute);
        }
        if (nonNull(omhRoomInfoResponse.getRoomSizeFeet())) {
            roomMetaAttributes.add(RoomMetaAttribute.builder()
                                       .providerAttributeId(ROOM_SIZE_FEET_PROVIDER_ATTRIBUTE_ID)
                                       .providerAttributeGroup(ROOM_SIZE_PROVIDER_ATTRIBUTE_GROUP)
                                       .providerLabel(String.format(ROOM_SIZE_FEET_PROVIDER_LABEL_FORMAT, omhRoomInfoResponse.getRoomSizeFeet()))
                                       .build());
        }
        if (nonNull(omhRoomInfoResponse.getRoomSizeMeter())) {
            roomMetaAttributes.add(RoomMetaAttribute.builder()
                                       .providerAttributeId(ROOM_SIZE_METER_PROVIDER_ATTRIBUTE_ID)
                                       .providerAttributeGroup(ROOM_SIZE_PROVIDER_ATTRIBUTE_GROUP)
                                       .providerLabel(String.format(ROOM_SIZE_METER_PROVIDER_LABEL_FORMAT, omhRoomInfoResponse.getRoomSizeFeet()))
                                       .build());
        }
        return roomMetaAttributes;
    }

    private List<RoomMetaImage> toRoomMetaImages(OmhRoomInfoResponse omhRoomInfoResponse) {
        List<RoomMetaImage> images = new ArrayList<>();
        List<OmhRoomPhoto> sortedPhotos = omhRoomInfoResponse.getPhotos().stream()
            .sorted(Comparator.comparing(OmhRoomPhoto::getOrder))
            .collect(Collectors.toList());

        for (int i = 0; i < sortedPhotos.size(); i++) {
            OmhRoomPhoto photo = sortedPhotos.get(i);
            RoomMetaImage image = RoomMetaImage.builder()
                .roomVisualCategory(RoomVisualCategory.NONE)
                .visualType(VisualType.IMAGE)
                .url(photo.getUrl())
                .displayable(true)
                .heroImage(i == 0)
                .enCaption(null)
                .koCaption(photo.getCaption())
                .build();
            images.add(image);
        }
        return images;
    }

    private List<RoomMetaBed> toBeds(OmhRoomInfoResponse omhRoomInfoResponse) {
        RoomMetaBed roomMetaBed = RoomMetaBed.builder()
            .koCaption(bedDescriptionConverter.toUnionStayBedDescription(omhRoomInfoResponse.getBedGroups()))
            .enCaption(null)
            .bedConfigurations(
                omhRoomInfoResponse.getBedGroups().stream()
                    .map(OmhBedGroup::getBeds)
                    .flatMap(Collection::stream)
                    .map(omhBed -> BedConfiguration.builder()
                        .size(String.valueOf(omhBed.getBedTypeSize()))
                        .type(StringUtils.isNotBlank(omhBed.getBedTypeNameByLanguage()) ?
                              omhBed.getBedTypeNameByLanguage() :
                              omhBed.getBedTypeName())
                        .quantity(omhBed.getBedTypeCount())
                        .build())
                    .collect(Collectors.toList())
            )
            .build();
        return List.of(roomMetaBed);
    }
}
