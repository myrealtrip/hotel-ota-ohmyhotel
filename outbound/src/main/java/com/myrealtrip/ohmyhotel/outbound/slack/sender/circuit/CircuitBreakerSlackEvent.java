package com.myrealtrip.ohmyhotel.outbound.slack.sender.circuit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CircuitBreakerSlackEvent {

    CIRCUIT_OPEN("써킷 브레이커 OPEN"),
    CIRCUIT_CLOSE("써킷 브레이커 CLOSE");

    private final String eventName;
}
