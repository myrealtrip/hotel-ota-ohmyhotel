package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelBulkListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse.OmhBulkHotel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 특정 날짜 이후 업데이트된 호텔 코드를 가져와 로컬에 저장한다.
 */
@RequiredArgsConstructor
@Slf4j
public class GetUpdatedHotelCodesTasklet implements Tasklet {

    private final HotelCodeStorage allHotelCodeStorage;
    private final OmhStaticHotelBulkListAgent omhStaticHotelBulkListAgent;
    private final LocalDate lastUpdateDate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long lastHotelCode = 0L;
        while (true) {
            OmhStaticBulkHotelListResponse response = omhStaticHotelBulkListAgent.getBulkHotels(lastUpdateDate, lastHotelCode);
            if (response.getHotelCount() == 0 ||
                (response.getHotelCount() == 1 && response.getHotels().get(0).getHotelCode().equals(lastHotelCode))) {
                break;
            }
            List<Long> hotelCodes = response.getHotels().stream()
                .map(OmhBulkHotel::getHotelCode)
                .collect(Collectors.toList());
            allHotelCodeStorage.addAll(hotelCodes);
            lastHotelCode = hotelCodes.get(hotelCodes.size() - 1);
            log.info("{}", hotelCodes);
        }
        log.info("total updated hotel size: {}", allHotelCodeStorage.getHotelCodes().size());
        return RepeatStatus.FINISHED;
    }
}
