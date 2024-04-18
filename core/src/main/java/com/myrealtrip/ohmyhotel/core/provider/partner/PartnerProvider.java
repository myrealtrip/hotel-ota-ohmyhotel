package com.myrealtrip.ohmyhotel.core.provider.partner;

import com.myrealtrip.ohmyhotel.core.infrastructure.partner.PartnerRepository;
import com.myrealtrip.ohmyhotel.core.provider.partner.mapper.PartnerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartnerProvider {

    private final PartnerMapper partnerMapper;
    private final PartnerRepository partnerRepository;
}
