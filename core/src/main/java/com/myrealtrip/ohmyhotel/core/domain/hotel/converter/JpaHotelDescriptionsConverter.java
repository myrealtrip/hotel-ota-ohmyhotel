package com.myrealtrip.ohmyhotel.core.domain.hotel.converter;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelDescriptions;
import com.myrealtrip.srtcommon.exceptions.JacksonProcessingException;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Collections;

public class JpaHotelDescriptionsConverter implements AttributeConverter<HotelDescriptions, String> {

    @Override
    public String convertToDatabaseColumn(HotelDescriptions hotelDescriptions) {
        return ObjectMapperUtils.writeAsString(hotelDescriptions);
    }

    @Override
    public HotelDescriptions convertToEntityAttribute(String dbData) {
        return ObjectMapperUtils.readString(dbData, HotelDescriptions.class);
    }
}
