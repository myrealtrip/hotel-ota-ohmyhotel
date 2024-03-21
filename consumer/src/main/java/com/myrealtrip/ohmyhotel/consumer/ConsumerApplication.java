package com.myrealtrip.ohmyhotel.consumer;

import com.myrealtrip.common.supports.servlet.MyrealtripCommonFeatureRegisterer;
import com.myrealtrip.common.supports.spring.EnableMyrealtripCommonFeatures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static com.myrealtrip.common.values.MyrealtripCommonFeature.GLOBAL_EXCEPTION_HANDLER;
import static com.myrealtrip.common.values.MyrealtripCommonFeature.HEALTH_CHECK_CONTROLLER;

@Import(MyrealtripCommonFeatureRegisterer.class)
@EnableMyrealtripCommonFeatures(features = { HEALTH_CHECK_CONTROLLER, GLOBAL_EXCEPTION_HANDLER })
@SpringBootApplication(scanBasePackages = { "com.myrealtrip" })
@Slf4j
public class ConsumerApplication {
    public static void main(String[] args) {
        try {
            ApplicationContext ctx = SpringApplication.run(ConsumerApplication.class, args);
        } catch (Exception exception) {
            log.error("", exception);
        }
    }
}
