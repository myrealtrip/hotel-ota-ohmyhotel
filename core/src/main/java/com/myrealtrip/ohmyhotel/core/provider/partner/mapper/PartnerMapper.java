package com.myrealtrip.ohmyhotel.core.provider.partner.mapper;

import com.myrealtrip.ohmyhotel.core.domain.partner.dto.Partner;
import com.myrealtrip.ohmyhotel.core.domain.partner.entity.PartnerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartnerMapper {

    Partner toDto(PartnerEntity entity);

    PartnerEntity toEntity(Partner dto);
}
