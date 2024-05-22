package com.myrealtrip.ohmyhotel.batch.tasklet;


import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class NotFoundHotelCodesLoggingTasklet implements Tasklet {

    private final HotelCodeStorage notFoundHotelCodesStorage;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String notFoundHotelIds = notFoundHotelCodesStorage.getHotelCodes().stream()
            .map(String::valueOf)
            .collect(Collectors.joining("\n"));

        log.info("notFoundHotelIds: \n{}", notFoundHotelIds);
        return RepeatStatus.FINISHED;
    }
}
