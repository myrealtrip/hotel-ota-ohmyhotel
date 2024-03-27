package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    EN("English"),
    KO("Korean"),
    JA("Japanese"),
    VI("Vietnamese"),
    ZH("Chinese(simplified)");

    private final String description;
}
