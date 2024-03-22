package com.myrealtrip.ohmyhotel.outbound.agent.common;

import com.myrealtrip.ohmyhotel.outbound.agent.common.AgentProperties;
import com.myrealtrip.srtcommon.support.webclient.WebClientBuilderFactory;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {

    public static WebClient createDefaultWebClient(AgentProperties properties, String baseUrl) {
        return WebClientBuilderFactory.builder()
            .maxMemorySize(properties.getMaxInMemorySize())
            .readTimeout(properties.getReadTimeout())
            .connectTimeout(properties.getConnectTimeout())
            .writeTimeout(properties.getWriteTimeout())
            .responseTimeout(properties.getResponseTimeout())
            .compressionEnabled(true)
            .build()
            .defaultWebClientBuilder()
            .baseUrl(baseUrl)
            .build();
    }
}
