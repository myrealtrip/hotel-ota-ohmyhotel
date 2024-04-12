package com.myrealtrip.ohmyhotel.outbound.agent.mrt.unionstay;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@Setter
@ConfigurationProperties("myrealtrip.api.unionstay")
public class UnionstayAgentProperties {

    private String baseUrl;
    private AgentProperties switching;
}
