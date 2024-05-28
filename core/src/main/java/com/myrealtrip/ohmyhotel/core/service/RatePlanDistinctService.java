package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.search.dto.RatePlan;
import com.myrealtrip.ohmyhotel.core.domain.search.dto.RatePlanDistinctField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatePlanDistinctService {

    private static final Comparator<RatePlan> RATE_PLAN_AMOUNT_COMPARATOR = Comparator.comparing(RatePlan::getTotalNetAmount);

    /**
     * 동일 객실 내에서 이름, 속성, 취환불규정이 동일한 rateplan이 있다면 가장 저렴한 것만 리턴한다.
     * @param ratePlanList 동일 객실에 속하는 RatePlan 목록
     */
    public List<String> getExposeRatePlanCode(List<RatePlan> ratePlanList) {
        Map<RatePlanDistinctField, List<RatePlan>> ratePlanGroupingMap = ratePlanList.stream()
            .collect(Collectors.groupingBy(RatePlan::getRatePlanDistinctField));

        List<String> result = new ArrayList<>();
        for (List<RatePlan> ratePlans : ratePlanGroupingMap.values()) {
            ratePlans.sort(RATE_PLAN_AMOUNT_COMPARATOR);
            result.add(ratePlans.get(0).getRatePlanCode());
        }

        return result;
    }
}
