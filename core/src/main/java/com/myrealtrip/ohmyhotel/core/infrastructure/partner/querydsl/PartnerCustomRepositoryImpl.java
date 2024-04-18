package com.myrealtrip.ohmyhotel.core.infrastructure.partner.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PartnerCustomRepositoryImpl implements PartnerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
