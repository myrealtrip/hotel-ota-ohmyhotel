package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.UniqueRunIdIncrementer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "SAMPLE_JOB")
public class SampleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job sampleJob() {
        return jobBuilderFactory.get("SAMPLE_JOB")
            .start(sampleStep())
            .incrementer(new UniqueRunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step sampleStep() {
        return stepBuilderFactory.get("SAMPLE_STEP")
            .tasklet(sampleTasklet())
            .build();
    }

    @Bean
    @StepScope
    public Tasklet sampleTasklet() {
        return (param1, param2) -> {
            log.info("this is sample job");
            return RepeatStatus.FINISHED;
        };
    }
}
