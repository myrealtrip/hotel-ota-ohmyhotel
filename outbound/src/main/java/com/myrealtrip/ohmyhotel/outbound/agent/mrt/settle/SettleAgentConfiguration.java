package com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle;

import com.myrealtrip.ohmyhotel.outbound.agent.common.WebClientFactory;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.unionstay.UnionstayAgentProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
@EnableConfigurationProperties(SettleAgentProperties.class)
@RequiredArgsConstructor
public class SettleAgentConfiguration {

    private final SettleAgentProperties settleAgentProperties;

    @Bean(name = "settleConfigWebClient")
    public WebClient settleConfigWebClient() {
        return WebClientFactory.createDefaultWebClient(settleAgentProperties.getSettlementConfig(), settleAgentProperties.getBaseUrl());
    }
}
