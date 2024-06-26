package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.enumarate.PenaltyBasis;
import com.myrealtrip.ohmyhotel.enumarate.PromotionType;
import com.myrealtrip.ohmyhotel.enumarate.RateOrAmount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy.OmhCancelPolicyValue;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.srtcommon.support.currency.CurrencyCode;
import com.myrealtrip.srtcommon.support.utils.NumericUtils;
import com.myrealtrip.srtcommon.support.utils.ZeroMarginUtils;
import com.myrealtrip.unionstay.common.constant.CancelPolicyType;
import com.myrealtrip.unionstay.common.constant.shop.DiscountType;
import com.myrealtrip.unionstay.common.model.common.MrtPromotion;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Commissions;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Commissions.UnionstayRatePriceMode;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RateBenefit;
import com.myrealtrip.unionstay.dto.hotelota.search.response.RoomAvailability;
import com.myrealtrip.unionstay.dto.hotelota.search.response.Surcharge;
import com.myrealtrip.unionstay.dto.hotelota.search.response.TotalPayment;
import com.myrealtrip.unionstay.dto.hotelota.search.response.cancelpolicy.CancelPolicy;
import com.myrealtrip.unionstay.dto.hotelota.search.response.cancelpolicy.OffsetCancelPolicy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class CommonSearchResponseConverter {

    public static final Comparator<RateAvailability> RATE_COMPARATOR = Comparator.comparing(rate -> rate.getTotalPayment().getInclusive());
    public static final Comparator<RoomAvailability> ROOM_COMPARATOR = Comparator.comparing(room -> room.getRates().get(0).getTotalPayment().getInclusive());

    private final MealBasisCodeConverter mealBasisCodeConverter;

    public List<RateBenefit> toRateBenefits(String mealBasisCodeStr) {
        MealBasisCode mealBasisCode = EnumUtils.getEnum(MealBasisCode.class, mealBasisCodeStr, MealBasisCode.NONE);
        Pair<String, String> mealAttribute = mealBasisCodeConverter.toAttribute(mealBasisCode);
        if (isNull(mealAttribute)) {
            return Collections.emptyList();
        }

        RateBenefit rateBenefit = RateBenefit.builder()
            .id(mealAttribute.getFirst())
            .group("MEAL_BASIS_CODE")
            .benefitName(mealAttribute.getSecond())
            .build();
        return List.of(rateBenefit);
    }

    public List<CancelPolicy> toCancelPolicies(OmhCancelPolicy omhCancelPolicy, BigDecimal mrtCommissionRate) {
        if (CollectionUtils.isEmpty(omhCancelPolicy.getPolicies())) {
            return Collections.emptyList();
        }
        PenaltyBasis penaltyBasis = omhCancelPolicy.getPenaltyBasis();
        ZoneId zoneId = ZoneId.of(omhCancelPolicy.getTimeZone());
        return omhCancelPolicy.getPolicies().stream()
            .map(omhCancelPolicyValue -> toCancelPolicy(omhCancelPolicyValue, penaltyBasis, zoneId, mrtCommissionRate))
            .collect(Collectors.toList());
    }

    private CancelPolicy toCancelPolicy(OmhCancelPolicyValue omhCancelPolicyValue, PenaltyBasis penaltyBasis, ZoneId zone, BigDecimal mrtCommissionRate) {
        CancelPolicyType cancelPolicyType;
        double value;
        if (omhCancelPolicyValue.getRateOrAmount() == RateOrAmount.RATE) {
            cancelPolicyType = penaltyBasis == PenaltyBasis.FIRST_NIGHT ?
                               CancelPolicyType.FIRST_NIGHT_PERCENT :
                               CancelPolicyType.PERCENT;
            value = omhCancelPolicyValue.getPenaltyValue().doubleValue();
        } else if (omhCancelPolicyValue.getRateOrAmount() == RateOrAmount.AMOUNT) {
            cancelPolicyType = CancelPolicyType.AMOUNT;
            value = OmhPriceCalculateUtils.toSalePrice(omhCancelPolicyValue.getPenaltyValue(), mrtCommissionRate).doubleValue();
        } else {
            throw new IllegalStateException("cannot mapping CancelPolicyType");
        }
        return OffsetCancelPolicy.builder()
            .start(omhCancelPolicyValue.getLocalDateTimeOfFromDateTime().atZone(zone).toOffsetDateTime())
            .end(omhCancelPolicyValue.getLocalDateTimeOfToDateTime().atZone(zone).toOffsetDateTime())
            .type(cancelPolicyType)
            .value(value)
            .build();
    }

    public TotalPayment toTotalPayment(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate) {
        BigDecimal salePrice = OmhPriceCalculateUtils.toSalePrice(omhTotalNetAmount, mrtCommissionRate);
        BigDecimal vat = OmhPriceCalculateUtils.toVat(salePrice);
        return TotalPayment.builder()
            .exclusive(salePrice.subtract(vat).doubleValue())
            .baseExclusive(salePrice.subtract(vat).doubleValue())
            .inclusive(salePrice.doubleValue())
            .baseInclusive(salePrice.doubleValue())
            .build();
    }

    public TotalPayment toZeroMarginTotalPayment(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate, ZeroMargin zeroMargin) {
        BigDecimal salePrice = OmhPriceCalculateUtils.toSalePrice(omhTotalNetAmount, mrtCommissionRate);
        BigDecimal zeroMarginSalePrice = ZeroMarginUtils.toZeroMarginSalePrice(omhTotalNetAmount, zeroMargin.getZeroMarginRate());
        BigDecimal vat = OmhPriceCalculateUtils.toVat(zeroMarginSalePrice);
        return TotalPayment.builder()
            .exclusive(zeroMarginSalePrice.subtract(vat).doubleValue())
            .baseExclusive(salePrice.subtract(vat).doubleValue())
            .inclusive(zeroMarginSalePrice.doubleValue())
            .baseInclusive(salePrice.doubleValue())
            .build();
    }

    public List<MrtPromotion> toZeroMarginMrtPromotions(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate, ZeroMargin zeroMargin) {
        BigDecimal salePrice = OmhPriceCalculateUtils.toSalePrice(omhTotalNetAmount, mrtCommissionRate);
        BigDecimal zeroMarginSalePrice = ZeroMarginUtils.toZeroMarginSalePrice(omhTotalNetAmount, zeroMargin.getZeroMarginRate());
        if (NumericUtils.equals(salePrice, zeroMarginSalePrice)) {
            return Collections.emptyList();
        }

        MrtPromotion mrtPromotion = MrtPromotion.builder()
            .discountType(DiscountType.ZERO_MARGIN)
            .mrtPromotionName(null)
            .mrtPromotionDescription(null)
            .totalDiscountAmount(salePrice.subtract(zeroMarginSalePrice))
            .build();
        return List.of(mrtPromotion);
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

    public List<Surcharge> toSurcharges(BigDecimal omhTotalNetAmount, BigDecimal mrtCommissionRate, ZeroMargin zeroMargin) {
        BigDecimal salePrice = OmhPriceCalculateUtils.toSalePrice(omhTotalNetAmount, mrtCommissionRate);
        BigDecimal vat;
        if (zeroMargin.isOn()) {
            BigDecimal zeroMarginSalePrice = ZeroMarginUtils.toZeroMarginSalePrice(omhTotalNetAmount, zeroMargin.getZeroMarginRate());
            vat = OmhPriceCalculateUtils.toVat(zeroMarginSalePrice);
        } else {
            vat = OmhPriceCalculateUtils.toVat(salePrice);
        }

        List<Surcharge> results = new ArrayList<>();
        results.add(Surcharge.builder()
                        .id(null)
                        .name("세금 및 수수료")
                        .margin(null)
                        .taxAndFee(true)
                        .currency(CurrencyCode.KRW)
                        .korInclusive(vat.doubleValue())
                        .inclusive(vat.doubleValue())
                        .exclusive(vat.doubleValue())
                        .tax(0d)
                        .fee(0d)
                        .charge(null)
                        .build());
        return results;
    }

    public String toUnionStayRoomName(String roomName, String roomNameByLanguage) {
        return StringUtils.isNotBlank(roomNameByLanguage) ?
               roomNameByLanguage :
               roomName;
    }

    public String toUnionStayOptionName(String roomName, String roomNameByLanguage, String ratePlanName, String ratePlanNameByLanguage) {
        String unionStayRoomName = toUnionStayRoomName(roomName, roomNameByLanguage);
        String unionStayOptionName = StringUtils.isNotBlank(ratePlanNameByLanguage) ?
                                     ratePlanNameByLanguage :
                                     ratePlanName;

        if (StringUtils.equals(unionStayRoomName, unionStayOptionName)) {
            return null;
        }
        return unionStayOptionName;
    }
}
