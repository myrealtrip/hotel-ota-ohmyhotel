package com.myrealtrip.ohmyhotel.enumarate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ZeroMarginType {
    MANUAL("개별 제로마진률을 적용한다."),
    GLOBAL("글로벌 제로마진률을 적용한다."),
    NOT_APPLY("제로마진을 적용하지 않는다.");

    private final String desc;
}
