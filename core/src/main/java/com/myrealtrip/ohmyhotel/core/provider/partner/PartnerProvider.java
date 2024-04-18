package com.myrealtrip.ohmyhotel.core.provider.partner;

import com.myrealtrip.ohmyhotel.core.domain.partner.dto.Partner;
import com.myrealtrip.ohmyhotel.core.domain.partner.entity.PartnerEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.partner.PartnerRepository;
import com.myrealtrip.ohmyhotel.core.provider.partner.mapper.PartnerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PartnerProvider {

    private final PartnerMapper partnerMapper;
    private final PartnerRepository partnerRepository;

    @Transactional
    public Partner getByPartnerId(Long partnerId) {
        return partnerRepository.findById(partnerId)
            .map(partnerMapper::toDto)
            .orElse(null);
    }

    @Transactional
    public void upsert(Partner partner) {
        PartnerEntity entity = partnerMapper.toEntity(partner);
        partnerRepository.save(entity);
    }
}
