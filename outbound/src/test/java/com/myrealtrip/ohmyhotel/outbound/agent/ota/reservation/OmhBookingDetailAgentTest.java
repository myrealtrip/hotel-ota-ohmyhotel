package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
@Disabled
class OmhBookingDetailAgentTest {

    @Autowired
    private OmhBookingDetailAgent agent;

    @Test
    void bookingDetail() {
        String mrtReservationNo = "ACM-00000000-0000021";

        OmhBookingDetailResponse response = agent.bookingDetail(mrtReservationNo);
        assertThat(response.getBookingCodes().getChannelBookingCode()).isEqualTo(mrtReservationNo);
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}