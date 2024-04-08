package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ImageUrlMapper {

    String PX70_URL_SUFFIX = "_t.jpg";
    String PX200_URL_SUFFIX = "_s.jpg";
    String PX350_URL_SUFFIX = "_b.jpg";
    String PX1000_URL_SUFFIX = "_z.jpg";

    default Map<Integer, String> toImageUrlMap(Photo photo) {
        if (!StringUtils.startsWith(photo.getUrl(), "http")) {
            return Map.of();
        }
        if (!StringUtils.endsWith(photo.getUrl(), PX1000_URL_SUFFIX)) {
            return Map.of(1000, photo.getUrl());
        }

        Map<Integer, String> imageUrlMap = new HashMap<>();
        imageUrlMap.put(1000, photo.getUrl());
        imageUrlMap.put(350, StringUtils.substringBefore(photo.getUrl(), PX1000_URL_SUFFIX) + PX350_URL_SUFFIX);
        imageUrlMap.put(200, StringUtils.substringBefore(photo.getUrl(), PX1000_URL_SUFFIX) + PX200_URL_SUFFIX);
        imageUrlMap.put(70, StringUtils.substringBefore(photo.getUrl(), PX1000_URL_SUFFIX) + PX70_URL_SUFFIX);
        return imageUrlMap;
    }
}
