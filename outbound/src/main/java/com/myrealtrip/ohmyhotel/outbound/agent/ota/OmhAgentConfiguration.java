package com.myrealtrip.ohmyhotel.outbound.agent.ota;

import com.myrealtrip.ohmyhotel.outbound.agent.common.WebClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = OmhAgentProperties.class)
public class OmhAgentConfiguration {

    private final OmhAgentProperties properties;

    @Bean
    public WebClient omhHealthCheckWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getHealthCheck(), properties.getBaseUrl());
    }
}
