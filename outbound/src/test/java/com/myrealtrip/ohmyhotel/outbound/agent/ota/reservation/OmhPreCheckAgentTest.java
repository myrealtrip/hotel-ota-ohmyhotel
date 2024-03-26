package com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
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
class OmhPreCheckAgentTest {

    @Autowired
    private OmhPreCheckAgent agent;

    @Test
    void preCheck() {
        OmhPreCheckRequest request = OmhPreCheckRequest.builder()
            .language(Language.KO)
            .checkInDate(LocalDate.now().plusDays(30))
            .checkOutDate(LocalDate.now().plusDays(32))
            .rooms(List.of(OmhRoomGuest.builder()
                               .adultCount(2)
                               .childCount(0)
                               .childAges(List.of())
                               .build()))
            .hotelCode(862813L)
            .rateType(RateType.STANDARD_RATE)
            .roomTypeCode("D_19499846")
            .ratePlanCode("500823^424084|2^0")
            .totalNetAmount(BigDecimal.valueOf(10000))
            .build();

        OmhPreCheckResponse response = agent.preCheck(request);
        assertThat(response.getStatus()).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}