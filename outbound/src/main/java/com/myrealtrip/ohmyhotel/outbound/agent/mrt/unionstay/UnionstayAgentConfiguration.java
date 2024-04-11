package com.myrealtrip.ohmyhotel.outbound.agent.mrt.unionstay;

import com.myrealtrip.ohmyhotel.outbound.agent.common.WebClientFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
@EnableConfigurationProperties(UnionstayAgentProperties.class)
@RequiredArgsConstructor
public class UnionstayAgentConfiguration {

    private final UnionstayAgentProperties unionstayAgentProperties;

    @Bean(name = "unionstaySwitchWebClient")
    public WebClient unionstaySwitchWebClient() {
        return WebClientFactory.createDefaultWebClient(unionstayAgentProperties.getSwitching(), unionstayAgentProperties.getBaseUrl());
    }
}
