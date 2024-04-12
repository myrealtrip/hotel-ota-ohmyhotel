package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.CustomZeroMarginProvider;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.GlobalZeroMarginProvider;
import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ZeroMarginSearchService {

    @Value("${zero-margin.global-zero-margin-id}")
    private Long globalZeroMarginId;

    /**
     * 글로벌 제로마진 설정
     */
    private GlobalZeroMargin globalZeroMargin;

    private final ZeroMarginAggregateService zeroMarginAggregateService;
    private final GlobalZeroMarginProvider globalZeroMarginProvider;
    private final CustomZeroMarginProvider customZeroMarginProvider;

    @PostConstruct
    private void postConstruct() {
        setGlobalZeroMargin();
    }

    /**
     * 해당 property 의 제로마진 설정을 조회합니다.
     * @param hotelId
     * @param useGlobalSwitch 제로마진 글로벌 스위치 사용 여부 (true 라면 글로벌 스위치를 참고하여 제로마진 on/off 를 결정합니다.)
     * @return
     */
    @Transactional
    public ZeroMargin getZeroMargin(Long hotelId, boolean useGlobalSwitch) {
        return getZeroMargins(List.of(hotelId), useGlobalSwitch).get(hotelId);
    }

    /**
     * 해당 property 의 제로마진 설정을 조회합니다.
     * @param hotelIds
     * @param useGlobalSwitch 제로마진 글로벌 스위치 사용 여부 (true 라면 글로벌 스위치를 참고하여 제로마진 on/off 를 결정합니다.)
     * @return key: propertyId, value: ZeroMarginDTO
     */
    @Transactional
    public Map<Long, ZeroMargin> getZeroMargins(List<Long> hotelIds, boolean useGlobalSwitch) {
        Map<Long, CustomZeroMargin> customZeroMarginMap = customZeroMarginProvider.getByPropertyIds(hotelIds).stream()
            .collect(Collectors.toMap(CustomZeroMargin::getHotelId, Function.identity()));

        return hotelIds.stream()
            .distinct()
            .collect(Collectors.toMap(
                propertyId -> propertyId,
                propertyId -> {
                    CustomZeroMargin customZeroMargin = customZeroMarginMap.getOrDefault(propertyId, null);
                    return useGlobalSwitch ?
                           zeroMarginAggregateService.aggregateWithGlobalSwitch(globalZeroMargin, customZeroMargin) :
                           zeroMarginAggregateService.aggregateWithoutGlobalSwitch(globalZeroMargin, customZeroMargin);
                }
            ));
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void setGlobalZeroMargin() {
        GlobalZeroMargin globalZeroMargin = globalZeroMarginProvider.getByGlobalZeroMarginId(globalZeroMarginId);
        if (isNull(globalZeroMargin)) {
            this.globalZeroMargin = GlobalZeroMargin.builder()
                .switchValue(SwitchValue.OFF)
                .build();
            return;
        }
        this.globalZeroMargin = globalZeroMargin;
    }
}
