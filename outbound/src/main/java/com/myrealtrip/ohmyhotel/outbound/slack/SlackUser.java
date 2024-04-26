package com.myrealtrip.ohmyhotel.outbound.slack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlackUser {

    KANG_MIN_SU("U01MNQ16PBN");

    private final String userId;
}
