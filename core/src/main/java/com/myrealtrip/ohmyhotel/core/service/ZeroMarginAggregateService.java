package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ZeroMarginAggregateService {

    /**
     * 제로마진 설정을 aggregate 한다.
     * 단, 적용여부를 따질 때 글로벌 스위치를 참고한다. (실시간 가격 검색에 사용)
     */
    public ZeroMargin aggregateWithGlobalSwitch(GlobalZeroMargin globalZeroMargin,
                                                CustomZeroMargin customZeroMargin) {
        ZeroMarginType type;
        if (isNull(customZeroMargin)) {
            type = globalZeroMargin.isOn() ? ZeroMarginType.GLOBAL : ZeroMarginType.NOT_APPLY;
        } else {
            type = customZeroMargin.getZeroMarginType() == ZeroMarginType.NOT_APPLY || globalZeroMargin.isOff() ?
                   ZeroMarginType.NOT_APPLY :
                   customZeroMargin.getZeroMarginType();
        }

        return ZeroMargin.builder()
            .type(type)
            .zeroMarginRate(aggregateZeroMarginMarkUpRate(globalZeroMargin, customZeroMargin, type))
            .build();
    }

    /**
     * 제로마진 설정을 aggregate 한다.
     * 단, 적용여부를 따질 때 글로벌 스위치를 참고하지 않는다. (메타 데이터 조회에 사용)
     */
    public ZeroMargin aggregateWithoutGlobalSwitch(GlobalZeroMargin globalZeroMargin,
                                                      CustomZeroMargin customZeroMargin) {
        ZeroMarginType type;
        if (isNull(customZeroMargin)) {
            type = ZeroMarginType.GLOBAL;
        } else {
            type = customZeroMargin.getZeroMarginType();
        }

        return ZeroMargin.builder()
            .type(type)
            .zeroMarginRate(aggregateZeroMarginMarkUpRate(globalZeroMargin, customZeroMargin, type))
            .build();
    }

    private BigDecimal aggregateZeroMarginMarkUpRate(GlobalZeroMargin globalZeroMargin,
                                                     CustomZeroMargin customZeroMargin,
                                                     ZeroMarginType finalZeroMarginType) {
        if (finalZeroMarginType == ZeroMarginType.NOT_APPLY) {
            return null;
        } else if (finalZeroMarginType == ZeroMarginType.GLOBAL) {
            return globalZeroMargin.getZeroMarginRate();
        } else {
            return customZeroMargin.getZeroMarginRate();
        }
    }
}
