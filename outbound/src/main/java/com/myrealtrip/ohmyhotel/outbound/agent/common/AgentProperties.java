package com.myrealtrip.ohmyhotel.outbound.agent.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentProperties {
    private int connectTimeout;
    private int writeTimeout;
    private int readTimeout;
    private int responseTimeout;
    private int maxInMemorySize;
}
