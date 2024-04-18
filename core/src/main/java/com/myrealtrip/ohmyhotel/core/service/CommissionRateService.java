package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.partner.dto.Partner;
import com.myrealtrip.ohmyhotel.core.provider.partner.PartnerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CommissionRateService {

    @Value("${ohmyhotel.partner-id}")
    private Long partnerId;

    private final PartnerProvider partnerProvider;

    @Transactional(readOnly = true)
    public BigDecimal getMrtCommissionRate() {
        return partnerProvider.getByPartnerId(partnerId).getCommissionRate();
    }
}
