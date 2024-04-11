package com.myrealtrip.ohmyhotel.core.provider.zeromargin;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.GlobalZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.GlobalZeroMarginEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.GlobalZeroMarginRepository;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper.GlobalZeroMarginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GlobalZeroMarginProvider {

    private final GlobalZeroMarginRepository globalZeroMarginRepository;
    private final GlobalZeroMarginMapper globalZeroMarginMapper;

    @Transactional(readOnly = true)
    public GlobalZeroMargin getByGlobalZeroMarginId(Long globalZeroMarginId) {
        return globalZeroMarginRepository.findById(globalZeroMarginId)
            .map(globalZeroMarginMapper::toDto)
            .orElse(null);
    }

    @Transactional
    public void upsert(GlobalZeroMargin globalZeroMargin) {
        GlobalZeroMarginEntity entity = globalZeroMarginMapper.toEntity(globalZeroMargin);
        globalZeroMarginRepository.save(entity);
    }
}
