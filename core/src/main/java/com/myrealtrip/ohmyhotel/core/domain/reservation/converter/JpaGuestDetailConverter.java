package com.myrealtrip.ohmyhotel.core.domain.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JpaGuestDetailConverter implements AttributeConverter<GuestDetail, String> {

    @Override
    public String convertToDatabaseColumn(GuestDetail guestDetail) {
        return ObjectMapperUtils.writeAsString(guestDetail);
    }

    @Override
    public GuestDetail convertToEntityAttribute(String dbData) {
        return ObjectMapperUtils.readString(dbData, GuestDetail.class);
    }
}
