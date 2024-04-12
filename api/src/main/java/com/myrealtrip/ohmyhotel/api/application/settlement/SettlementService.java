package com.myrealtrip.ohmyhotel.api.application.settlement;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.CustomZeroMarginProvider;
import com.myrealtrip.ohmyhotel.core.service.MrtDiscountTypesUpdateKafkaSendService;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.common.constant.ProviderType;
import com.myrealtrip.unionstay.common.constant.SettlementApplyType;
import com.myrealtrip.unionstay.common.constant.SettlementConfigType;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigGetRequest;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigItem;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigRequest;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final CustomZeroMarginProvider customZeroMarginProvider;
    private final MrtDiscountTypesUpdateKafkaSendService mrtDiscountTypesUpdateKafkaSendService;
    private final ZeroMarginSearchService zeroMarginSearchService;

    @Transactional
    public void upsert(SettlementConfigRequest request) {
        if (!(request.getProviderType() == ProviderType.GDS && request.getProviderCode() == ProviderCode.OH_MY_HOTEL)) {
            log.info("providerType: {}, providerCode: {}", request.getProviderType(), request.getProviderCode());
            throw new IllegalArgumentException("providerType, providerCode 를 확인해주세요.");
        }

        List<Long> propertyIdsForCustomZeroMarginUpsert = getHotelIdsForCustomZeroMarginUpsert(request);
        if (CollectionUtils.isEmpty(propertyIdsForCustomZeroMarginUpsert)) {
            return;
        }
        Map<Long, CustomZeroMargin> existCustomZeroMarginMap = customZeroMarginProvider.getByPropertyIds(propertyIdsForCustomZeroMarginUpsert)
            .stream()
            .collect(Collectors.toMap(CustomZeroMargin::getHotelId, Function.identity()));

        List<CustomZeroMargin> insertCustomZeroMargins = getInsertCustomZeroMargins(request, existCustomZeroMarginMap);
        List<CustomZeroMargin> updateCustomZeroMargins = getUpdateCustomZeroMargins(request, existCustomZeroMarginMap);
        customZeroMarginProvider.upsert(ListUtils.union(insertCustomZeroMargins, updateCustomZeroMargins));
        mrtDiscountTypesUpdateKafkaSendService.sendByHotelIds(propertyIdsForCustomZeroMarginUpsert);
    }

    public SettlementConfigResponse getSettlementConfig(SettlementConfigGetRequest request) {
        if (request.getProviderType() != request.getProviderType() ||
            request.getProviderCode() != ProviderCode.OH_MY_HOTEL ||
            request.getConfigType() != SettlementConfigType.ZERO_MARGIN) {
            log.info("{}", ObjectMapperUtils.writeAsString(request));
            throw new IllegalArgumentException("처리할 수 없는 요청 입니다.");
        }
        ZeroMargin zeroMargin = zeroMarginSearchService.getZeroMargin(Long.valueOf(request.getProviderPropertyId()), false);
        return SettlementConfigResponse.builder()
            .providerType(ProviderType.GDS)
            .providerCode(ProviderCode.OH_MY_HOTEL)
            .providerPropertyId(request.getProviderPropertyId())
            .configType(SettlementConfigType.ZERO_MARGIN)
            .applyType(toSettlementApplyType(zeroMargin.getType()))
            .commissionRate(zeroMargin.getZeroMarginRate())
            .mrtCommissionRate(null) // TODO 수수료 확인 후 세팅
            .build();
    }

    private List<Long> getHotelIdsForCustomZeroMarginUpsert(SettlementConfigRequest request) {
        return request.getSettlementConfigs().stream()
            .filter(configItem -> configItem.getConfigType() == SettlementConfigType.ZERO_MARGIN)
            .map(SettlementConfigItem::getProviderPropertyId)
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }

    private List<CustomZeroMargin> getInsertCustomZeroMargins(SettlementConfigRequest request,
                                                                 Map<Long, CustomZeroMargin> existCustomZeroMarginMap) {
        return request.getSettlementConfigs().stream()
            .filter(configItem -> configItem.getConfigType() == SettlementConfigType.ZERO_MARGIN)
            .filter(configItem -> !existCustomZeroMarginMap.containsKey(Long.valueOf(configItem.getProviderPropertyId())))
            .map(configItem -> CustomZeroMargin.builder()
                .hotelId(Long.valueOf(configItem.getProviderPropertyId()))
                .zeroMarginRate(configItem.getCommissionRate())
                .zeroMarginType(toZeroMarginType(configItem.getApplyType()))
                .build())
            .collect(Collectors.toList());
    }

    private List<CustomZeroMargin> getUpdateCustomZeroMargins(SettlementConfigRequest request,
                                                                 Map<Long, CustomZeroMargin> existCustomZeroMarginMap) {
        return request.getSettlementConfigs().stream()
            .filter(configItem -> configItem.getConfigType() == SettlementConfigType.ZERO_MARGIN)
            .filter(configItem -> existCustomZeroMarginMap.containsKey(Long.valueOf(configItem.getProviderPropertyId())))
            .map(configItem -> {
                Long propertyId = Long.valueOf(configItem.getProviderPropertyId());
                CustomZeroMargin existCustomZeroMargin = existCustomZeroMarginMap.get(propertyId);
                return CustomZeroMargin.builder()
                    .customZeroMarginId(existCustomZeroMargin.getCustomZeroMarginId())
                    .hotelId(Long.valueOf(configItem.getProviderPropertyId()))
                    .zeroMarginRate(configItem.getCommissionRate())
                    .zeroMarginType(toZeroMarginType(configItem.getApplyType()))
                    .build();
            })
            .collect(Collectors.toList());
    }

    private ZeroMarginType toZeroMarginType(SettlementApplyType settlementApplyType) {
        if (settlementApplyType == SettlementApplyType.NOT_APPLY) {
            return ZeroMarginType.NOT_APPLY;
        }
        if (settlementApplyType == SettlementApplyType.MANUAL) {
            return ZeroMarginType.MANUAL;
        }
        if (settlementApplyType == SettlementApplyType.DEFAULT) {
            return ZeroMarginType.GLOBAL;
        }
        throw new IllegalStateException();
    }

    private SettlementApplyType toSettlementApplyType(ZeroMarginType zeroMarginType) {
        if (zeroMarginType == ZeroMarginType.NOT_APPLY) {
            return SettlementApplyType.NOT_APPLY;
        }
        if (zeroMarginType == ZeroMarginType.MANUAL) {
            return SettlementApplyType.MANUAL;
        }
        if (zeroMarginType == ZeroMarginType.GLOBAL) {
            return SettlementApplyType.DEFAULT;
        }
        throw new IllegalStateException();
    }
}
