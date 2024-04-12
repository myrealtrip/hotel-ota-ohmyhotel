package com.myrealtrip.ohmyhotel.api.application.settlement;

import com.myrealtrip.ohmyhotel.api.protocol.settlement.GlobalZeroMarginResponse;
import com.myrealtrip.ohmyhotel.api.protocol.settlement.GlobalZeroMarginUpsertRequest;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.GlobalZeroMarginProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.unionstay.UnionstaySwitchAgent;
import com.myrealtrip.unionstay.dto.hotelota.switching.UnionstaySwitchKey;
import com.myrealtrip.unionstay.dto.hotelota.switching.UnionstaySwitchKey.UnionstaySwitchValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class GlobalZeroMarginService {

    @Value("${zero-margin.global-zero-margin-id}")
    private Long globalZeroMarginId;

    @Value("${spring.profiles.active}")
    private String profile;

    private final GlobalZeroMarginProvider globalZeroMarginProvider;
    private final UnionstaySwitchAgent unionstaySwitchAgent;

    @Transactional
    public void upsertGlobalZeroMargin(GlobalZeroMarginUpsertRequest globalZeroMarginUpsertRequest) {
        GlobalZeroMargin newGlobalZeroMargin = GlobalZeroMargin.builder()
            .globalZeroMarginId(globalZeroMarginId)
            .zeroMarginRate(globalZeroMarginUpsertRequest.getZeroMarginRate())
            .switchValue(globalZeroMarginUpsertRequest.getSwitchValue())
            .applyEnv(profile)
            .build();

        globalZeroMarginProvider.upsert(newGlobalZeroMargin);
    }

    @Transactional(readOnly = true)
    public GlobalZeroMarginResponse getGlobalZeroMargin() {
        GlobalZeroMargin globalZeroMargin = globalZeroMarginProvider.getByGlobalZeroMarginId(globalZeroMarginId);
        if (isNull(globalZeroMargin)) {
            return null;
        }
        return GlobalZeroMarginResponse.builder()
            .zeroMarginRate(globalZeroMargin.getZeroMarginRate())
            .switchValue(globalZeroMargin.getSwitchValue())
            .build();
    }

    @Transactional
    public void callUnionstayZeroMarginSwitch() {
        GlobalZeroMargin globalZeroMargin = globalZeroMarginProvider.getByGlobalZeroMarginId(globalZeroMarginId);
        if (isNull(globalZeroMargin)) {
            throw new IllegalStateException("GlobalZeroMargin 이 존재하지 않습니다.");
        }
        unionstaySwitchAgent.updateSwitch(
            UnionstaySwitchKey.ZERO_MARGIN_OH_MY_HOTEL,
            globalZeroMargin.isOn() ? UnionstaySwitchValue.A : UnionstaySwitchValue.B
        );
    }
}
