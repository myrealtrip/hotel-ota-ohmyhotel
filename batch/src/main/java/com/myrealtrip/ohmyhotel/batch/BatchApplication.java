package com.myrealtrip.ohmyhotel.batch;

import com.myrealtrip.common.presentation.exceptionhandlers.ExceptionLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Slf4j
@EnableBatchProcessing
@Import({ ExceptionLogger.class })
@SpringBootApplication(scanBasePackages = { "com.myrealtrip" })
public class BatchApplication extends DefaultBatchConfigurer {

    @Value("${spring.batch.job.names:NONE}")
    private String jobNames;

    @PostConstruct
    void validate() {
        log.info("jobNames: {}", jobNames);
        if (StringUtils.isBlank(jobNames) || StringUtils.equals("NONE", jobNames)) {
            throw new IllegalStateException("spring.batch.job.names에 1개 이상의 Job이 필요합니다.");
        }
    }

    public static void main(String... args) {
        System.exit(SpringApplication.exit(
            new SpringApplicationBuilder(BatchApplication.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.LOG)
                .build(args)
                .run(args)
        ));
    }

    @Override
    public void setDataSource(DataSource dataSource) {
    }

    @Bean
    @Primary
    public JpaTransactionManager batchTransactionManager() {
        return new JpaTransactionManager();
    }
}