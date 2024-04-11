package com.myrealtrip.ohmyhotel.core.provider.zeromargin;

import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.CustomZeroMarginRepository;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper.CustomZeroMarginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomZeroMarginProvider {

    private final CustomZeroMarginMapper customZeroMarginMapper;
    private final CustomZeroMarginRepository customZeroMarginRepository;
}
