package com.myrealtrip.ohmyhotel.core.infrastructure.partner;

import com.myrealtrip.ohmyhotel.core.domain.partner.entity.PartnerEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.partner.querydsl.PartnerCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, Long>, PartnerCustomRepository {
}
