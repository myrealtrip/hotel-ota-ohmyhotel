package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GlobalZeroMarginCustomRepositoryImpl implements GlobalZeroMarginCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
