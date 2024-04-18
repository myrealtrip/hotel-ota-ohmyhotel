package com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("myrealtrip.api.settle")
public class SettleAgentProperties {

    private String baseUrl;
    private AgentProperties settlementConfig;
}
