package com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle;

import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.settle.web.values.SaleCommissionPolicyInquiryResponse;
import com.myrealtrip.settle.web.values.SettlementConfigResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
class SettleConfigAgentTest {

    @Autowired
    private SettleConfigAgent agent;

    @Test
    void getSettlementConfig() {
        String partnerId = "3147309";

        SaleCommissionPolicyInquiryResponse response = agent.getSettlementConfig(partnerId);

        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}