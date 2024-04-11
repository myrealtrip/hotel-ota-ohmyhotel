package com.myrealtrip.ohmyhotel.core.provider.zeromargin;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.CustomZeroMargin;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.CustomZeroMarginRepository;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper.CustomZeroMarginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomZeroMarginProvider {

    private final CustomZeroMarginMapper customZeroMarginMapper;
    private final CustomZeroMarginRepository customZeroMarginRepository;

    @Transactional
    public List<CustomZeroMargin> getByPropertyIds(List<Long> propertyIds) {
        return customZeroMarginRepository.findAllByHotelIds(propertyIds)
            .stream()
            .map(customZeroMarginMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<CustomZeroMargin> upsert(List<CustomZeroMargin> customZeroMargins) {
        List<CustomZeroMarginEntity> customZeroMarginEntities = customZeroMargins.stream()
            .map(customZeroMarginMapper::toEntity)
            .collect(Collectors.toList());

        return customZeroMarginRepository.saveAll(customZeroMarginEntities).stream()
            .map(customZeroMarginMapper::toDto)
            .collect(Collectors.toList());
    }
}
