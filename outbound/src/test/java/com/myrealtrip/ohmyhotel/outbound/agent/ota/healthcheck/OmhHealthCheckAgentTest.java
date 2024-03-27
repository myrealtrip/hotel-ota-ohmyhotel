package com.myrealtrip.ohmyhotel.outbound.agent.ota.healthcheck;

import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.healthcheck.protocol.OmhHealthCheckResponse;
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
class OmhHealthCheckAgentTest {

    @Autowired
    private OmhHealthCheckAgent omhHealthCheckAgent;

    @Test
    void healthCheck() {
        OmhHealthCheckResponse res = omhHealthCheckAgent.healthCheck();
        assertThat(res.isSuccess()).isTrue();
        log.info("{}", ObjectMapperUtils.writeAsString(res));
    }
}