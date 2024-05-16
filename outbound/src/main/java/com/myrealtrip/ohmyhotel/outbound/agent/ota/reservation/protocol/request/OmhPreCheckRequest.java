package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuestCount;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class OmhPreCheckRequest {

    @Builder.Default
    private String nationalityCode = "KR";

    private Language language;

    private Long hotelCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    private String roomTypeCode;

    private String roomToken;

    private String ratePlanCode;

    private List<OmhRoomGuestCount> rooms;

    private RateType rateType;

    private BigDecimal totalNetAmount;
}
