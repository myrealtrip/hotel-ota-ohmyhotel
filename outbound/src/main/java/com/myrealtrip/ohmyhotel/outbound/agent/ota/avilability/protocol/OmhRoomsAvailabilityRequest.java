package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OmhRoomsAvailabilityRequest {

    @Builder.Default
    private String nationalityCode = "KR";

    private Language language;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private List<RoomGuest> rooms;

    private RateType rateType;

    private Long hotelCode;
}
