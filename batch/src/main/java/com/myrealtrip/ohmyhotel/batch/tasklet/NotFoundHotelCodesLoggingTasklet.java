package com.myrealtrip.ohmyhotel.batch.tasklet;


import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@RequiredArgsConstructor
public class NotFoundHotelCodesLoggingTasklet implements Tasklet {

    private final HotelCodeStorage notFoundHotelCodesStorage;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        return null;
    }
}
