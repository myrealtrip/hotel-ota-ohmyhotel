package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.unionstay.common.constant.MrtDiscountType;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.message.property.UpdatePropertyMrtDiscountTypesMessage;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PropertyMrtDiscountTypesUpdateMessageMapper {

    /**
     * UpdatePropertyMrtDiscountTypesMessage 로 변환한다.
     *
     * @param zeroMarginMap key: propertyId, value: 제로마진 설정
     * @return
     */
    default UpdatePropertyMrtDiscountTypesMessage toMessage(Map<Long, ZeroMargin> zeroMarginMap) {
        return UpdatePropertyMrtDiscountTypesMessage.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .items(zeroMarginMap.entrySet().stream()
                       .map(entry -> UpdatePropertyMrtDiscountTypesMessage.Item.builder()
                           .providerPropertyId(String.valueOf(entry.getKey()))
                           .mrtDiscountTypes(toMrtDiscountTypes(entry.getValue().isOn()))
                           .build())
                       .collect(Collectors.toList()))
            .lastUpdatedAt(LocalDateTime.now())
            .build();
    }

    private List<String> toMrtDiscountTypes(boolean zeroMarginApply) {
        return zeroMarginApply ? List.of(MrtDiscountType.ZERO_MARGIN.name()) : List.of();
    }
}