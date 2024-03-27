package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingRequestCode {

    BRQ01("Non-smoking Room"),
    BRQ02("Smoking Room"),
    BRQ03("Late Check In"),
    BRQ04("High Floor"),
    BRQ05("HoneyMoon"),
    BRQ06("Baby Cot"),
    BRQ99("Others");

    private final String description;
}
