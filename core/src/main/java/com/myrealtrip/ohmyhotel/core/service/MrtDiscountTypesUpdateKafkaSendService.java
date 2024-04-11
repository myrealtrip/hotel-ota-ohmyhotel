package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.outbound.producer.CommonProducer;
import com.myrealtrip.unionstay.common.message.property.UpdatePropertyMrtDiscountTypesMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MrtDiscountTypesUpdateKafkaSendService {

    private final CommonProducer commonProducer;
    private final ZeroMarginSearchService zeroMarginSearchService;
    private final PropertyMrtDiscountTypesUpdateMessageMapper messageMapper;

    public void sendByHotelIds(List<Long> hotelIds) {
        Map<Long, ZeroMargin> zeroMarginMap = zeroMarginSearchService.getZeroMargins(hotelIds, false);
        UpdatePropertyMrtDiscountTypesMessage message = messageMapper.toMessage(zeroMarginMap);
        commonProducer.publishMrtDiscountTypesUpdate(message);

        String ids = hotelIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(", "));

        log.info("property-mrt-discount-types-update send");
        log.info("{}", ids);
    }
}
