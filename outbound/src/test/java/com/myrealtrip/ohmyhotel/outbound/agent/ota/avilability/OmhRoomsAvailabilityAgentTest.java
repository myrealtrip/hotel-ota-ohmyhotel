package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.RoomGuest;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
            .rooms(List.of(RoomGuest.builder()
                               .adultCount(2)
                               .childCount(0)
                               .childAges(List.of())
                               .build()))
            .hotelCode(862813L)
            .rateType(RateType.STANDARD_RATE)
            .build();

        OmhRoomsAvailabilityResponse response = agent.getAvailability(request);
        assertThat(response.getRooms()).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}