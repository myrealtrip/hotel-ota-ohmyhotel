package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelBulkListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticBulkHotelListResponse.OmhBulkHotel;
import lombok.RequiredArgsConstructor;
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
public class GetUpdatedHotelCodesTasklet implements Tasklet {

    private final HotelCodeStorage hotelCodeStorage;
    private final OmhStaticHotelBulkListAgent omhStaticHotelBulkListAgent;
    private final LocalDate lastUpdateDate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Long lastHotelCode = 0L;
        while (true) {
            OmhStaticBulkHotelListResponse response = omhStaticHotelBulkListAgent.getBulkHotels(lastUpdateDate, lastHotelCode);
            if (response.getHotelCount() == 0) {
                break;
            }
            List<Long> hotelCodes = response.getHotels().stream()
                .map(OmhBulkHotel::getHotelCode)
                .collect(Collectors.toList());
            hotelCodeStorage.saveAll(hotelCodes);
            lastHotelCode = hotelCodes.get(hotelCodes.size() - 1);
        }
        return RepeatStatus.FINISHED;
    }
}
