package com.myrealtrip.ohmyhotel.api.application.meta.converter;

import com.myrealtrip.ohmyhotel.core.service.BedDescriptionConverter;
import com.myrealtrip.ohmyhotel.enumarate.BedTypeCode;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhBedGroup;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomFacility;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomInfoResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomInfoResponse.OmhRoomPhoto;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.constant.RoomStatus;
import com.myrealtrip.unionstay.common.constant.RoomVisualCategory;
import com.myrealtrip.unionstay.common.constant.VisualType;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaAttribute;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaBed;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaBed.BedConfiguration;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaGuestPolicy;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaImage;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.myrealtrip.ohmyhotel.constants.AttributeConstants.*;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class RoomMetaResponseConverter {

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
        if (nonNull(omhRoomInfoResponse.getFacilities())) {
            for (OmhRoomFacility omhRoomFacility : omhRoomInfoResponse.getFacilities()) {
                RoomMetaAttribute roomMetaAttribute = RoomMetaAttribute.builder()
                    .providerAttributeGroup(ROOM_FACILITY_PROVIDER_ATTRIBUTE_GROUP)
                    .providerAttributeId(omhRoomFacility.getFacilityCode())
                    .providerLabel(StringUtils.isNotBlank((omhRoomFacility.getFacilityNameByLanguage())) ?
                                   omhRoomFacility.getFacilityNameByLanguage() :
                                   omhRoomFacility.getFacilityName())
                    .build();
                roomMetaAttributes.add(roomMetaAttribute);
            }
        }
        if (nonNull(omhRoomInfoResponse.getRoomSizeFeet()) && omhRoomInfoResponse.getRoomSizeFeet() > 0) {
            roomMetaAttributes.add(RoomMetaAttribute.builder()
                                       .providerAttributeId(ROOM_SIZE_FEET_PROVIDER_ATTRIBUTE_ID)
                                       .providerAttributeGroup(ROOM_SIZE_PROVIDER_ATTRIBUTE_GROUP)
                                       .providerLabel(String.format(ROOM_SIZE_FEET_PROVIDER_LABEL_FORMAT, omhRoomInfoResponse.getRoomSizeFeet().longValue()))
                                       .build());
        }
        if (nonNull(omhRoomInfoResponse.getRoomSizeMeter()) && omhRoomInfoResponse.getRoomSizeMeter() > 0) {
            roomMetaAttributes.add(RoomMetaAttribute.builder()
                                       .providerAttributeId(ROOM_SIZE_METER_PROVIDER_ATTRIBUTE_ID)
                                       .providerAttributeGroup(ROOM_SIZE_PROVIDER_ATTRIBUTE_GROUP)
                                       .providerLabel(String.format(ROOM_SIZE_METER_PROVIDER_LABEL_FORMAT, omhRoomInfoResponse.getRoomSizeMeter().longValue()))
                                       .build());
        }
        return roomMetaAttributes;
    }

    private List<RoomMetaImage> toRoomMetaImages(OmhRoomInfoResponse omhRoomInfoResponse) {
        if (CollectionUtils.isEmpty(omhRoomInfoResponse.getPhotos())) {
            return Collections.emptyList();
        }
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
                        .type(toBedType(omhBed))
                        .quantity(omhBed.getBedTypeCount())
                        .build())
                    .collect(Collectors.toList())
            )
            .build();
        return List.of(roomMetaBed);
    }

    private String toBedType(OmhBedGroup.OmhBed bed) {
        if (nonNull(bed.getBedTypeCodeEnum()) && bed.getBedTypeCodeEnum() != BedTypeCode.NONE) {
            return bed.getBedTypeCodeEnum().getExposedName();
        }
        if (nonNull(BedTypeCode.getByDescription(bed.getBedTypeName()))) {
            return BedTypeCode.getByDescription(bed.getBedTypeName()).getExposedName();
        }
        return bed.getBedTypeName();
    }
}
