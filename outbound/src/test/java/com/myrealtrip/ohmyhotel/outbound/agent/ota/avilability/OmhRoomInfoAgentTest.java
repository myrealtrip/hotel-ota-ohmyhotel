package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomInfoResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
@Disabled
class OmhRoomInfoAgentTest {

    @Autowired
    private OmhRoomInfoAgent agent;

    @Test
    void getRoomInfo() {
        OmhRoomInfoRequest request = OmhRoomInfoRequest.builder()
            .language(Language.KO)
            .hotelCode(862813L)
            .roomTypeCode("D_19499846")
            .ratePlanCode("500823^424084|2^0")
            .build();

        OmhRoomInfoResponse response = agent.getRoomInfo(request);
        assertThat(response).isNotNull();
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}