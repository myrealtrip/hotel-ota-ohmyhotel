package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol;

import com.myrealtrip.ohmyhotel.enumarate.Gender;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhCreateBookingContactPerson;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhRoomGuestDetail;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhRoomGuestInfo;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCreateBookingResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
@Disabled
class OmhCreateBookingAgentTest {

    @Autowired
    private OmhCreateBookingAgent agent;

    @Test
    void createBooking() {
        OmhCreateBookingRequest request = OmhCreateBookingRequest.builder()
            .language(Language.KO)
            .channelBookingCode("ACM-00000000-0000022")
            .contactPerson(OmhCreateBookingContactPerson.builder()
                               .name("kang min su")
                               .email("minsu.kang@myrealtrip.com")
                               .mobileNo("01012345678")
                               .build())
            .hotelCode(862813L)
            .checkInDate(LocalDate.of(2024, 5, 5))
            .checkOutDate(LocalDate.of(2024, 5, 7))
            .roomTypeCode("D_19499846")
            .ratePlanCode("500823^424084|2^0")
            .freeBreakfastName(null)
            .rooms(List.of(OmhRoomGuestInfo.builder()
                               .roomNo(1)
                               .guests(List.of(OmhRoomGuestDetail.builder()
                                                   .lastName("Kang")
                                                   .firstName("Minsu")
                                                   .gender(Gender.M)
                                                   .birthday(LocalDate.of(1997, 8, 16))
                                                   .build()))
                               .build()))
            .requests(List.of())
            .rateType(RateType.STANDARD_RATE)
            .totalNetAmount(BigDecimal.valueOf(351100))
            .build();

        OmhCreateBookingResponse response = agent.crateBooking(request);
        assertThat(response.isSuccess()).isTrue();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}