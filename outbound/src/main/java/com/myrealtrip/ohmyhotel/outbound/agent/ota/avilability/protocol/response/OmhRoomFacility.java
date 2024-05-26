package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhRoomFacility {
    private String facilityCode;
    private String facilityName;
    private String facilityNameByLanguage;
}
