package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhCancelBookingResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
class OmhCancelBookingAgentTest {

    @Autowired
    private OmhCancelBookingAgent agent;

    @Test
    void cancelBooking() {
        String mrtReservationNo = "ACM-00000000-0000021";

        OmhCancelBookingResponse response = agent.cancelBooking(mrtReservationNo);

        assertThat(response.getCancelConfirmNo()).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}