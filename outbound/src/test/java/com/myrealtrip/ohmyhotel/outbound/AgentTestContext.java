package com.myrealtrip.ohmyhotel.outbound;

import com.myrealtrip.ohmyhotel.outbound.slack.SlackNotifierConfiguration;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.circuit.CircuitBreakerSlackSender;
import com.myrealtrip.srtcommon.support.webclient.CommonCircuitBreakerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = "com.myrealtrip.ohmyhotel.outbound.agent")
@Import({ SlackNotifierConfiguration.class, CommonCircuitBreakerConfig.class, CircuitBreakerSlackSender.class })
public class AgentTestContext {
}
