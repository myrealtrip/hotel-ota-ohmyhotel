package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.slack.client.SlackNotifier;
import com.myrealtrip.slack.client.factory.SlackNotifierFactory;
import com.myrealtrip.slack.client.type.NotifierType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.myrealtrip.ohmyhotel.outbound.slack.SlackChannel.CHANNEL_WEBHOOK_TEST;
import static org.junit.jupiter.api.Assertions.*;

class HotelCancelAlertTaskletTest {

    @Test
    void alert_test() throws Exception {
        MrtReservationNoStorage mrtReservationNoStorage = new MrtReservationNoStorage();
        mrtReservationNoStorage.addAll(List.of("ACM-20240507-00000292", "ACM-20240507-00000293", "ACM-20240507-00000294"));
        SlackNotifier slackNotifier = SlackNotifierFactory.createWithChannelKey(NotifierType.SYNC, CHANNEL_WEBHOOK_TEST);
        HotelCancelAlertTasklet hotelCancelAlertTasklet = new HotelCancelAlertTasklet(slackNotifier, "local", "batch", mrtReservationNoStorage);
        hotelCancelAlertTasklet.execute(null, null);
    }
}