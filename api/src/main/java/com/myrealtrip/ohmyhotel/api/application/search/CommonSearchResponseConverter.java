package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.PenaltyBasis;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateOrAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse.OmhRoomSimpleAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy.OmhCancelPolicyDetail;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.unionstay.common.constant.CancelPolicyType;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Commissions;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Commissions.UnionstayRatePriceMode;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateBenefit;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RoomAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.TotalPayment;
import com.myrealtrip.unionstay.dto.hotelota.search.response.cancelpolicy.CancelPolicy;
import com.myrealtrip.unionstay.dto.hotelota.search.response.cancelpolicy.OffsetCancelPolicy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class CommonSearchResponseConverter {

    public static final Comparator<RateAvailability> RATE_COMPARATOR = Comparator.comparing(rate -> rate.getTotalPayment().getInclusive());

    public static final Comparator<RoomAvailability> ROOM_COMPARATOR = Comparator.comparing(room -> room.getRates().get(0).getTotalPayment().getInclusive());

    public List<RateBenefit> toRateBenefits(MealBasisCode mealBasisCode) {
        if (isNull(mealBasisCode) ||
            StringUtils.isBlank(mealBasisCode.getExposedName())) {
            return Collections.emptyList();
        }
        RateBenefit rateBenefit = RateBenefit.builder()
            .id(mealBasisCode.name())
            .group("MEAL_BASIS_CODE")
            .benefitName(mealBasisCode.getExposedName())
            .attributeValue(null)
            .build();
        return List.of(rateBenefit);
    }

    public List<CancelPolicy> toCancelPolicies(OmhCancelPolicy omhCancelPolicy) {
        if (CollectionUtils.isEmpty(omhCancelPolicy.getPolicies())) {
            return Collections.emptyList();
        }
        PenaltyBasis penaltyBasis = omhCancelPolicy.getPenaltyBasis();
        ZoneId zoneId = ZoneId.of(omhCancelPolicy.getTimeZone());
        return omhCancelPolicy.getPolicies().stream()
            .map(omhCancelPolicyDetail -> toCancelPolicy(omhCancelPolicyDetail, penaltyBasis, zoneId))
            .collect(Collectors.toList());
    }

    private CancelPolicy toCancelPolicy(OmhCancelPolicyDetail omhCancelPolicyDetail, PenaltyBasis penaltyBasis, ZoneId zone) {
        CancelPolicyType cancelPolicyType;
        if (omhCancelPolicyDetail.getRateOrAmount() == RateOrAmount.RATE) {
            cancelPolicyType = penaltyBasis == PenaltyBasis.FIRST_NIGHT ?
                               CancelPolicyType.FIRST_NIGHT_PERCENT :
                               CancelPolicyType.PERCENT;
        } else if (omhCancelPolicyDetail.getRateOrAmount() == RateOrAmount.AMOUNT){
            cancelPolicyType = CancelPolicyType.AMOUNT;
        } else {
            throw new IllegalStateException("cannot mapping CancelPolicyType");
        }
        return OffsetCancelPolicy.builder()
            .start(omhCancelPolicyDetail.getFromDateTime().atZone(zone).toOffsetDateTime())
            .end(omhCancelPolicyDetail.getToDateTime().atZone(zone).toOffsetDateTime())
            .type(cancelPolicyType)
            .value(omhCancelPolicyDetail.getPenaltyValue().doubleValue())
            .build();
    }

    public TotalPayment toTotalPayment(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate) {
        double salePrice = OmhPriceCalculateUtils.toSalePrice(omhTotalNetAmount, mrtCommissionRate).doubleValue();
        return TotalPayment.builder()
            .exclusive(salePrice)
            .baseExclusive(salePrice)
            .inclusive(salePrice)
            .baseInclusive(salePrice)
            .build();
    }

    public Commissions toCommissions(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate) {
        return Commissions.builder()
            .reservationProfit(OmhPriceCalculateUtils.toMrtCommission(omhTotalNetAmount, mrtCommissionRate))
            .commissionRate(mrtCommissionRate)
            .ratePriceMode(UnionstayRatePriceMode.DEPOSIT_PRICE)
            .build();
    }

    public Integer toMinNights(PromotionType promotionType, Integer promotionValue) {
        if (promotionType == PromotionType.MIN_NIGHTS) {
            return promotionValue;
        }
        return null;
    }
}
