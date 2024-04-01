package com.myrealtrip.ohmyhotel.core.domain.hotel.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class JpaPhotoListConverter implements AttributeConverter<List<Photo>, String> {

    private final TypeReference<List<Photo>> ref = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<Photo> photos) {
        return ObjectMapperUtils.writeAsString(photos);
    }

    @Override
    public List<Photo> convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData)) {
            return Collections.emptyList();
        }
        return ObjectMapperUtils.readString(dbData, ref);
    }
}
