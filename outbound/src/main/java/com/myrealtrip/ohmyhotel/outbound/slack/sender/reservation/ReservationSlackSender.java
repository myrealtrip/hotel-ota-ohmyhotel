package com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation;

import com.myrealtrip.ohmyhotel.outbound.slack.SlackMention;
import com.myrealtrip.slack.client.SlackNotifier;
import com.myrealtrip.slack.core.Color;
import com.myrealtrip.slack.core.utils.SlackMessageUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
public class ReservationSlackSender {

    private final SlackNotifier reservationSrtSlackNotifier;
    private final SlackNotifier reservationCxSlackNotifier;
    private final String profile;
    private final String context;

    public ReservationSlackSender(@Qualifier("reservationSrtSlackNotifier") SlackNotifier reservationSrtSlackNotifier,
                                  @Qualifier("reservationCxSlackNotifier") SlackNotifier reservationCxSlackNotifier,
                                  @Value("${spring.profiles.active:none}") String profile,
                                  @Value("${myrealtrip.ohmyhotel.context:none}") String context) {
        this.reservationSrtSlackNotifier = reservationSrtSlackNotifier;
        this.reservationCxSlackNotifier = reservationCxSlackNotifier;
        this.profile = profile;
        this.context = context;
    }

    public void sendToSrtWithMention(ReservationSlackEvent event, String mrtReservationNo, String payload) {
        sendToSrtWithMention(event, mrtReservationNo, payload, Color.RED);
    }

    public void sendToSrtWithMention(ReservationSlackEvent event, String mrtReservationNo, String payload, Color color) {
        List<String> messages = createSlackMessages(event, mrtReservationNo, payload, true);
        reservationSrtSlackNotifier.send(messages, color);
    }

    public void sendToSrt(ReservationSlackEvent event, String mrtReservationNo, String payload) {
        sendToSrt(event, mrtReservationNo, payload, Color.RED);
    }

    public void sendToSrt(ReservationSlackEvent event, String mrtReservationNo, String payload, Color color) {
        List<String> messages = createSlackMessages(event, mrtReservationNo, payload, false);
        reservationSrtSlackNotifier.send(messages, color);
    }

    public void sendToCx(ReservationSlackEvent event, String mrtReservationNo, String payload) {
        List<String> messages = createSlackMessages(event, mrtReservationNo, payload, false);
        reservationCxSlackNotifier.send(messages, Color.RED);
    }

    private List<String> createSlackMessages(ReservationSlackEvent event, String mrtReservationNo, String payload, boolean mention) {
        List<String> messages = SlackMessageUtils.messages(event.getEventName(), profile);
        String service = mention && (profile.contains("prod") || profile.contains("stage")) ?
                         SlackMessageUtils.bold("ohmyhotel") + " " + SlackMention.RESERVATION_GROUP.mention() :
                         SlackMessageUtils.bold("ohmyhotel");
        messages.add("service: " + service);
        messages.add("context: " + SlackMessageUtils.bold(context));
        messages.add("마리트 예약번호: " + SlackMessageUtils.bold(mrtReservationNo));
        messages.add("Note: " + event.getNote());
        if (nonNull(payload)) {
            messages.add("payload: " + payload);
        }
        return messages;
    }
}
