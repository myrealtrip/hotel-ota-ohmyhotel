package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuest;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
class OmhHotelsAvailabilityAgentTest {

    @Autowired
    private OmhHotelsAvailabilityAgent agent;

    @Test
    void getAvailability() {
        OmhHotelsAvailabilityRequest request = OmhHotelsAvailabilityRequest.builder()
            .language(Language.KO)
            .checkInDate(LocalDate.now().plusDays(30))
            .checkOutDate(LocalDate.now().plusDays(32))
            .rooms(List.of(OmhRoomGuest.builder()
                               .adultCount(2)
                               .childCount(0)
                               .childAges(List.of())
                               .build()))
            .hotelCodes(List.of(862813L))
            .rateType(RateType.STANDARD_RATE)
            .build();

        OmhHotelsAvailabilityResponse response = agent.getAvailability(request);
        assertThat(response.getHotels()).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}