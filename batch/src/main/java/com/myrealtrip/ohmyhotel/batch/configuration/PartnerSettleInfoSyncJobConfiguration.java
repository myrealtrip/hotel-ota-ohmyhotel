package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.tasklet.PartnerSettleInfoSyncTasklet;
import com.myrealtrip.ohmyhotel.core.provider.partner.PartnerProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle.SettleConfigAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static com.myrealtrip.ohmyhotel.batch.configuration.PartnerSettleInfoSyncJobConfiguration.PARTNER_SETTLE_INFO_SYNC_JOB;

/**
 * 파트너 정산정보를 싱크한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = PARTNER_SETTLE_INFO_SYNC_JOB)
public class PartnerSettleInfoSyncJobConfiguration {

    public static final String PARTNER_SETTLE_INFO_SYNC_JOB = "PARTNER_SETTLE_INFO_SYNC_JOB";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job partnerSettleInfoSyncJob() {
        return jobBuilderFactory.get(PARTNER_SETTLE_INFO_SYNC_JOB)
            .start(partnerSettleInfoSyncStep())
            .build();
    }

    @Bean
    @JobScope
    public Step partnerSettleInfoSyncStep() {
        return stepBuilderFactory.get("partnerSettleInfoSyncStep")
            .transactionManager(transactionManager)
            .tasklet(partnerSettleInfoSyncTasklet(null, null, null))
            .build();
    }

    @Bean
    @StepScope
    public Tasklet partnerSettleInfoSyncTasklet(@Value("${ohmyhotel.partner-id}") String partnerId,
                                                PartnerProvider partnerProvider,
                                                SettleConfigAgent settleConfigAgent) {
        return new PartnerSettleInfoSyncTasklet(partnerId, partnerProvider, settleConfigAgent);
    }
}
