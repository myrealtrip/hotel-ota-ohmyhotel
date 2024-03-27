package com.myrealtrip.ohmyhotel.outbound.slack;

import com.myrealtrip.slack.client.SlackNotifier;
import com.myrealtrip.slack.client.factory.SlackNotifierFactory;
import com.myrealtrip.slack.client.type.NotifierType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.myrealtrip.ohmyhotel.outbound.slack.SlackChannel.*;

/**
 * 채널 설명: https://myrealtrip.atlassian.net/wiki/spaces/STP/pages/3405119915/notification
 */
@Configuration
public class SlackNotifierConfiguration {
    @Value("${myrealtrip.staynet.context:NONE}")
    private String context;

    private NotifierType type() {
        if (context == null || "NONE".equals(context) || context.equals("batch")) {
            return NotifierType.SYNC;
        }

        return NotifierType.ASYNC;
    }

    @Bean(name = "webhookTestSlackNotifier")
    public SlackNotifier webhookTestSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), CHANNEL_WEBHOOK_TEST);
    }

    /* 모니터링_숙박_개발 */
    @Bean(name = "commonSlackNotifier")
    @Profile("local | dev | dev01 | dev02 | test | test01 | test02")
    public SlackNotifier devTestCommonSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), COMMON_DEV_TEST);
    }

    /* 모니터링_숙박_운영 */
    @Bean(name = "commonSlackNotifier")
    @Profile("stage | prod")
    public SlackNotifier prodCommonSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), COMMON_PROD);
    }

    /* 모니터링_숙박_예약_개발 */
    @Bean(name = "reservationSrtSlackNotifier")
    @Profile("local | dev | dev01 | dev02 | test | test01 | test02")
    public SlackNotifier devTestReservationSrtSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), RESERVATION_DEV_TEST);
    }

    /* 모니터링_숙박_예약_srt_운영 */
    @Bean(name = "reservationSrtSlackNotifier")
    @Profile("stage | prod")
    public SlackNotifier prodReservationSrtSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), RESERVATION_SRT_PROD);
    }

    /* 모니터링_숙박_예약_개발 */
    @Bean(name = "reservationCxSlackNotifier")
    @Profile("local | dev | dev01 | dev02 | test | test01 | test02")
    public SlackNotifier devTestReservationCxSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), RESERVATION_DEV_TEST);
    }

    /* 모니터링_숙박_예약_cx_운영 */
    @Bean(name = "reservationCxSlackNotifier")
    @Profile("stage | prod")
    public SlackNotifier prodReservationCxSlackNotifier() {
        return SlackNotifierFactory.createWithChannelKey(type(), RESERVATION_CX_PROD);
    }
}
