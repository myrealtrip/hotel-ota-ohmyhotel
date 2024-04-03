package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * information (호텔 상세정보) 가 없는 호텔들을 INACTIVE 처리한다.
 */
@RequiredArgsConstructor
public class InformationNotExistHotelProcessTasklet implements Tasklet {

    private final HotelCodeStorage informationExistHotelCodeStorage;
    private final HotelProvider hotelProvider;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Long> allHotelIds = hotelProvider.getAllHotelIds();
        List<Long> informationNotExistHotelIds = allHotelIds.stream()
            .filter(hotelId -> !informationExistHotelCodeStorage.getHotelCodes().contains(hotelId))
            .collect(Collectors.toList());

        hotelProvider.updateStatusByHotelIds(informationNotExistHotelIds, HotelStatus.INACTIVE);
        return RepeatStatus.FINISHED;
    }
}
