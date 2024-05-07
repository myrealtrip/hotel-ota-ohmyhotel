package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.outbound.slack.SlackMention;
import com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation.ReservationSlackEvent;
import com.myrealtrip.slack.client.SlackNotifier;
import com.myrealtrip.slack.core.utils.SlackMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HotelCancelAlertTasklet implements Tasklet {

    private final SlackNotifier reservationCxSlackNotifier;
    private final String profile;
    private final String context;
    private final MrtReservationNoStorage mrtReservationNoStorage;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<String> messages = createSlackMessages();
        reservationCxSlackNotifier.send(messages);
        return RepeatStatus.FINISHED;
    }

    private List<String> createSlackMessages() {
        ReservationSlackEvent event = ReservationSlackEvent.HOTEL_CANCEL_CHECK;
        String mrtReservationNos = String.join("\n", mrtReservationNoStorage.getMrtReservationNos());
        String service = SlackMessageUtils.bold("ohmyhotel") + " " + SlackMention.RESERVATION_GROUP.mention();

        List<String> messages = SlackMessageUtils.messages(event.getEventName(), profile);
        messages.add("service: " + service);
        messages.add("context: " + SlackMessageUtils.bold(context));
        messages.add("Note: " + event.getNote());
        messages.add("총 개수: " + mrtReservationNoStorage.getMrtReservationNos().size());
        messages.add(mrtReservationNos);
        return messages;
    }
}
