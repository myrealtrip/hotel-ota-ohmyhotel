package com.myrealtrip.ohmyhotel.core.provider.zeromargin;

import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.GlobalZeroMarginRepository;
import com.myrealtrip.ohmyhotel.core.provider.zeromargin.mapper.GlobalZeroMarginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalZeroMarginProvider {

    private final GlobalZeroMarginRepository globalZeroMarginRepository;
    private final GlobalZeroMarginMapper globalZeroMarginMapper;
}
