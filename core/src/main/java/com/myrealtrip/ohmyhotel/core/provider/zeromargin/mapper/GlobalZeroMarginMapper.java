package com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.GlobalZeroMarginEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GlobalZeroMarginMapper {

    GlobalZeroMargin toDto(GlobalZeroMarginEntity entity);

    GlobalZeroMarginEntity toEntity(GlobalZeroMargin dto);
}
