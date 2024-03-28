package com.myrealtrip.ohmyhotel.core.domain.hotel.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Facility;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Collections;
import java.util.List;

public class JpaFacilityListConverter implements AttributeConverter<List<Facility>, String> {

    private final TypeReference<List<Facility>> ref = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<Facility> facilities) {
        return ObjectMapperUtils.writeAsString(facilities);
    }

    @Override
    public List<Facility> convertToEntityAttribute(String dbData) {
        if (StringUtils.isEmpty(dbData)) {
            return Collections.emptyList();
        }
        return ObjectMapperUtils.readString(dbData, ref);
    }
}
