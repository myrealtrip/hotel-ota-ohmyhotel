package com.myrealtrip.ohmyhotel.core.domain.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JpaGuestCountConverter implements AttributeConverter<GuestCount, String> {

    @Override
    public String convertToDatabaseColumn(GuestCount guestCount) {
        return ObjectMapperUtils.writeAsString(guestCount);
    }

    @Override
    public GuestCount convertToEntityAttribute(String dbData) {
        return ObjectMapperUtils.readString(dbData, GuestCount.class);
    }
}
