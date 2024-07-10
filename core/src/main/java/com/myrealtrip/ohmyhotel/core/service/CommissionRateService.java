package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties;
import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.LocalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCacheable;
import com.myrealtrip.ohmyhotel.core.domain.partner.dto.MrtCommissionInfo;
import com.myrealtrip.ohmyhotel.core.domain.partner.dto.Partner;
import com.myrealtrip.ohmyhotel.core.provider.partner.PartnerProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle.SettleConfigAgent;
import com.myrealtrip.settle.web.values.SaleCommissionPolicyInquiryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommissionRateService {

    @Value("${ohmyhotel.partner-id}")
    private Long partnerId;

    private final PartnerProvider partnerProvider;

    private final SettleConfigAgent settleConfigAgent;

    @Transactional
    public BigDecimal getMrtCommissionRate() {
        return getMrtCommissionInfo().getCommissionRate();
    }

    /**
     * 정산 API 로 부터 커미션 설정을 가져온다.
     * 실패할 경우 DB 에 저장해둔 백업본을 사용한다.
     * @return
     */
    @Transactional
    @LocalCacheable(cache = LocalCache.MRT_COMMISSION_INFO, param = "null", type = MrtCommissionInfo.class)
    public MrtCommissionInfo getMrtCommissionInfo() {
        MrtCommissionInfo mrtCommissionInfo;
        try {
            mrtCommissionInfo = getMrtCommissionInfoFromSettlementApi();
        } catch (Throwable t) {
            log.error("settle api error", t);
            Partner partner =  partnerProvider.getByPartnerId(partnerId);
            return new MrtCommissionInfo(partner.getCommissionRate(), -1L);
        }

        if (mrtCommissionInfo.getSaleCommissionPolicyId() == -1) {
            Partner partner =  partnerProvider.getByPartnerId(partnerId);
            return new MrtCommissionInfo(partner.getCommissionRate(), -1L);
        }
        return mrtCommissionInfo;
    }

    private MrtCommissionInfo getMrtCommissionInfoFromSettlementApi() {
        SaleCommissionPolicyInquiryResponse response = settleConfigAgent.getSettlementConfig(String.valueOf(partnerId));
        return new MrtCommissionInfo(response.getCommissionRate(), response.getSaleCommissionPolicyId());
    }
}
