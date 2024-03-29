package com.myrealtrip.ohmyhotel.outbound.agent.ota;

import com.myrealtrip.ohmyhotel.outbound.agent.common.WebClientFactory;
import lombok.RequiredArgsConstructor;
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

    @Bean
    public WebClient omhStaticHotelBulkListWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getStaticHotelBulkList(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhStaticHotelInfoListWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getStaticHotelInfoList(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhHotelsAvailabilityWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getHotelsAvailability(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhRoomsAvailabilityWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getRoomsAvailability(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhRoomInfoWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getRoomInfo(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhPreCheckWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getPreCheck(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhCreateBookingWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getCreateBooking(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhBookingDetailWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getBookingDetail(), properties.getBaseUrl());
    }

    @Bean
    public WebClient omhCancelBookingWebClient() {
        return WebClientFactory.createDefaultWebClient(properties.getCancelBooking(), properties.getBaseUrl());
    }
}
