package com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomZeroMarginMapper {

    CustomZeroMargin toDto(CustomZeroMarginEntity entity);

    CustomZeroMarginEntity toEntity(CustomZeroMargin dto);
}
