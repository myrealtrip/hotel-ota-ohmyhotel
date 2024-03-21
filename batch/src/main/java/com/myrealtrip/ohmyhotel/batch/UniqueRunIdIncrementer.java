package com.myrealtrip.ohmyhotel.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

/**
 * 동일 Job Parameters 로 Job을 다시 실행시키기 위해 사용합니다.
 * https://jojoldu.tistory.com/487 참고
 */
public class UniqueRunIdIncrementer extends RunIdIncrementer {
    private static final String RUN_ID = "run.id";

    @Override
    public JobParameters getNext(JobParameters parameters) {
        JobParameters params = (parameters == null) ? new JobParameters() : parameters;
        return new JobParametersBuilder()
            .addLong(RUN_ID, params.getLong(RUN_ID, Long.valueOf(0)) + 1)
            .toJobParameters();
    }
}
