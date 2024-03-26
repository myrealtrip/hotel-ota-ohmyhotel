package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OmhHotelsAvailabilityRequest {

    @Builder.Default
    private String nationalityCode = "KR";

    private Language language;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private List<OmhRoomGuest> rooms;

    private RateType rateType;

    private List<Long> hotelCodes;
}
