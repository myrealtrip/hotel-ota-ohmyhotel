package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.unionstay.common.constant.CountryCode;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderPropertyStatus;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage.Address;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage.Contact;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage.GuestPolicy;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage.Image;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Mapper(componentModel = "spring")
public interface UpsertPropertyMessageMapper {

    ImageUrlMapper imageUrlMapper = Mappers.getMapper(ImageUrlMapper.class);
    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UpsertPropertyMessageMapper.class);
    DateTimeFormatter CHECK_IN_OUT_FORMATTER = DateTimeFormatter.ofPattern("[H:mm][HH:mm]");

    default UpsertPropertyMessage toUpsertPropertyMessage(Hotel hotel) {
        return UpsertPropertyMessage.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.UNKNOWN) // TODO
            .providerPropertyId(String.valueOf((hotel.getHotelId())))
            .providerPropertyStatus(hotel.isActive() ? ProviderPropertyStatus.ON_SALE : ProviderPropertyStatus.OFF_SALE)
            .chainId(null)
            .chainName(null)
            .brandId(null)
            .brandName(null)
            .formerName(hotel.getKoName())
            .enName(hotel.getEnName())
            .koName(hotel.getKoName())
            .onSitePaymentCurrency(null)
            .timeZone(null)
            .timeZoneOffset(null)
            .guestPolicy(toGuestPolicy(hotel))
            .address(toAddress(hotel))
            .contact(toContact(hotel))
            .starRating(StringUtils.isEmpty(hotel.getStarRating()) ? null : Float.parseFloat(hotel.getStarRating()))
            .descriptions(null) // TODO DescriptionType 매핑필요
            .images(toImages(hotel))
            .attributes(null) // TODO Attribute 매핑 필요
            .localInformations(Collections.emptyList())
            .ranking(null)
            .overallRating(null)
            .cleanlinessRating(null)
            .serviceRating(null)
            .comfortRating(null)
            .conditionRating(null)
            .locationRating(null)
            .neighborhoodRating(null)
            .qualityRating(null)
            .valueRating(null)
            .amenityRating(null)
            .recommendationPercent(null)
            .ratingCount(null)
            .lastUpdatedAt(hotel.getUpdatedAt())
            .discountable(true)
            .forceUpdate(true)
            .mrtDiscountTypes(null) // TODO 제로마진 스펙 추가 후 작업
            .mrtPartnerId(null) // TODO 파트너 ID 생성 후 작업
            .build();
    }

    private List<Image> toImages(Hotel hotel) {
        List<UpsertPropertyMessage.Image> images = new ArrayList<>();
        List<Photo> sortedPhoto = hotel.getPhotos().stream()
            .sorted(Comparator.comparing(Photo::getOrder))
            .collect(Collectors.toList());

        for (int i = 0; i < sortedPhoto.size(); i++) {
            Photo photo = sortedPhoto.get(i);
            Image image = Image.builder()
                .imageId(null)
                .propertyImageType(null)
                .priority(i)
                .urls(imageUrlMapper.toImageUrlMap(photo))
                .enCaption(null)
                .koCaption(photo.getCaption())
                .heroImage(i == 0)
                .build();
            images.add(image);
        }
        return images.stream()
            .filter(image -> MapUtils.isNotEmpty(image.getUrls()))
            .collect(Collectors.toList());
    }

    private Contact toContact(Hotel hotel) {
        return Contact.builder()
            .emails(Collections.emptyList())
            .faxes(isNull(hotel.getFaxNo()) ? Collections.emptyList() : List.of(hotel.getFaxNo()))
            .phones(isNull(hotel.getPhoneNo()) ? Collections.emptyList() : List.of(hotel.getPhoneNo()))
            .urls(isNull(hotel.getHomepageUrl()) ? Collections.emptyList() : List.of(hotel.getHomepageUrl()))
            .build();
    }

    private Address toAddress(Hotel hotel) {
        return UpsertPropertyMessage.Address.builder()
            .countryCode(CountryCode.getByIso2CountryCode(hotel.getCountryCode()))
            .stateProvinceCode(null)
            .postalCode(hotel.getZipCode())
            .enCountry(null)
            .koCountry(null)
            .koStateProvince(null)
            .enStateProvince(null)
            .koCity(null)
            .enCity(null)
            .enStreetName(null)
            .koStreetName(null)
            .streetNumber(null)
            .koAddressLines(isNull(hotel.getKoAddress()) ? Collections.emptyList() : List.of(hotel.getKoAddress()))
            .enAddressLines(isNull(hotel.getEnAddress()) ? Collections.emptyList() : List.of(hotel.getEnAddress()))
            .latitude(hotel.getLatitude())
            .longitude(hotel.getLongitude())
            .build();
    }

    default GuestPolicy toGuestPolicy(Hotel hotel) {
        return UpsertPropertyMessage.GuestPolicy.builder()
            .checkInMinAge(null)
            .checkInStartTime(toCheckInStartTime(hotel))
            .checkInEndTime(toCheckInEndTime(hotel))
            .checkOutStartTime(toCheckOutStartTime(hotel))
            .checkOutEndTime(toCheckOutEndTime(hotel))
            .displayCheckInStartTime(null)
            .displayCheckInEndTime(null)
            .displayCheckOutStartTime(null)
            .displayCheckOutEndTime(null)
            .build();
    }

    default LocalTime toCheckInStartTime(Hotel hotel) {
        if (StringUtils.isBlank(hotel.getCheckInTime())) {
            return null;
        }
        String checkInStartTimeStr = StringUtils.substringBefore(hotel.getCheckInTime(), "~");
        return toLocalTime(hotel.getHotelId(), checkInStartTimeStr);
    }

    default LocalTime toCheckInEndTime(Hotel hotel) {
        if (StringUtils.isBlank(hotel.getCheckInTime())) {
            return null;
        }
        if (hotel.getCheckInTime().contains("~")) {
            String checkInEndTimeStr = StringUtils.substringAfter(hotel.getCheckInTime(), "~");
            return toLocalTime(hotel.getHotelId(), checkInEndTimeStr);
        }
        return null;
    }

    default LocalTime toCheckOutStartTime(Hotel hotel) {
        if (StringUtils.isBlank(hotel.getCheckOutTime())) {
            return null;
        }
        if (hotel.getCheckOutTime().contains("~")) {
            String checkOutStartTimeStr = StringUtils.substringBefore(hotel.getCheckOutTime(), "~");
            return toLocalTime(hotel.getHotelId(), checkOutStartTimeStr);
        }
        return null;
    }

    default LocalTime toCheckOutEndTime(Hotel hotel) {
        if (StringUtils.isBlank(hotel.getCheckOutTime())) {
            return null;
        }
        if (hotel.getCheckOutTime().contains("~")) {
            String checkOutEndTimeStr = StringUtils.substringAfter(hotel.getCheckOutTime(), "~");
            return toLocalTime(hotel.getHotelId(), checkOutEndTimeStr);
        }
        return toLocalTime(hotel.getHotelId(), hotel.getCheckOutTime());
    }

    default LocalTime toLocalTime(Long hotelId, String checkInOutTimeStr) {
        if (checkInOutTimeStr.contains("AM")) {
            String timeStr = StringUtils.replace(
                StringUtils.substringBefore(checkInOutTimeStr, "AM"),
                " ",
                ""
            );
            return LocalTime.parse(timeStr, CHECK_IN_OUT_FORMATTER);
        }
        if (checkInOutTimeStr.contains("PM")) {
            String timeStr = StringUtils.replace(
                StringUtils.substringBefore(checkInOutTimeStr, "PM"),
                " ",
                ""
            );
            LocalTime time = LocalTime.parse(timeStr, CHECK_IN_OUT_FORMATTER);
            return time.isAfter(LocalTime.of(11, 59)) ? time : time.plusHours(12);
        }
        if (checkInOutTimeStr.contains("midnight")) {
            return LocalTime.of(0, 0);
        }
        try {
            return LocalTime.parse(StringUtils.replace(checkInOutTimeStr, " ", ""));
        } catch (Exception e) {
            log.error("hotel {} LocalTime parse fail", hotelId, e);
            return null;
        }
    }
}
