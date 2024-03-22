package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.OmhStaticBulkHotelListResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
class OmhStaticHotelBulkListAgentTest {

    @Autowired
    private OmhStaticHotelBulkListAgent hotelBulkListAgent;

    @Test
    void getBulkHotels() {
        OmhStaticBulkHotelListResponse res = hotelBulkListAgent.getBulkHotels(LocalDate.of(2024, 3, 1), 0L);
        assertThat(res.isSuccess()).isTrue();
        log.info("totalHotelCount: {}", res.getHotelCount());
    }
}