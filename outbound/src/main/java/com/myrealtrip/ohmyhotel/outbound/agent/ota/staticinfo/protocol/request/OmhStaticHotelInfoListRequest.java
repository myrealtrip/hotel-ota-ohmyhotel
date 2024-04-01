package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class OmhStaticHotelInfoListRequest {

    private Language language;

    private List<Long> hotelCodes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastUpdateDate;

    public static OmhStaticHotelInfoListRequest create(Language language, List<Long> hotelCodes) {
        return OmhStaticHotelInfoListRequest.builder()
            .language(language)
            .hotelCodes(hotelCodes)
            .lastUpdateDate(LocalDate.of(1970, 1, 1))
            .build();
    }
}
