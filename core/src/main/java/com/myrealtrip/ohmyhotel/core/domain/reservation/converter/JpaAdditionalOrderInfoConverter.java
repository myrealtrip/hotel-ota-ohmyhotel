package com.myrealtrip.ohmyhotel.core.domain.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JpaAdditionalOrderInfoConverter implements AttributeConverter<AdditionalOrderInfo, String> {

    @Override
    public String convertToDatabaseColumn(AdditionalOrderInfo additionalOrderInfo) {
        return ObjectMapperUtils.writeAsString(additionalOrderInfo);
    }

    @Override
    public AdditionalOrderInfo convertToEntityAttribute(String dbData) {
        return ObjectMapperUtils.readString(dbData, AdditionalOrderInfo.class);
    }
}
