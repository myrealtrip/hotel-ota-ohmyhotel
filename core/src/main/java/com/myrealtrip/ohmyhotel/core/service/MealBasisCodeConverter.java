package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.constants.AttributeConstants;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static java.util.Objects.isNull;

@Component
public class MealBasisCodeConverter {

    /**
     * MealBasisCode 를 통합숙소 Attribute 로 변환한다. <br>
     * first: attribute id <br>
     * second: attribute name <br>
     * @return
     */
    public Pair<String, String> toAttribute(MealBasisCode mealBasisCode) {
        if (mealBasisCode == MealBasisCode.NONE) {
            return null;
        }
        String attributeId;
        String attributeName;
        if (isNull(mealBasisCode) || mealBasisCode == MealBasisCode.Z || mealBasisCode == MealBasisCode.N) {
            attributeId = AttributeConstants.RATE_PLAN_NO_BREAK_FAST_PROVIDER_ATTRIBUTE_ID;
            attributeName = AttributeConstants.RATE_PLAN_NO_BREAK_FAST_PROVIDER_ATTRIBUTE_NAME;
        } else {
            attributeId = mealBasisCode.name();
            attributeName = mealBasisCode.getExposedName();
        }
        return Pair.of(attributeId, attributeName);
    }
}
