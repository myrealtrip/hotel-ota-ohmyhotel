package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OmhStaticHotelInfoListRequest {

    private Language language;

    private List<Long> hotelCodes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastUpdateDate;
}
