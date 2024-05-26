package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuestCount;
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
class OmhRoomsAvailabilityAgentTest {

    @Autowired
    private OmhRoomsAvailabilityAgent agent;

    @Test
    void getAvailability() {
        OmhRoomsAvailabilityRequest request = OmhRoomsAvailabilityRequest.builder()
            .language(Language.KO)
            .checkInDate(LocalDate.now().plusDays(30))
            .checkOutDate(LocalDate.now().plusDays(32))
            .rooms(List.of(OmhRoomGuestCount.builder()
                               .adultCount(2)
                               .childCount(0)
                               .childAges(List.of())
                               .build()))
            .hotelCode(862813L)
            .rateType(RateType.STANDARD_RATE)
            .build();

        OmhRoomsAvailabilityResponse response = agent.getRoomsAvailability(request);
        assertThat(response.getRooms()).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}