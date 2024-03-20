package com.myrealtrip.ohmyhotel.api;

import com.myrealtrip.common.supports.servlet.MyrealtripCommonFeatureRegisterer;
import com.myrealtrip.common.supports.spring.EnableMyrealtripCommonFeatures;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.DispatcherServlet;

import static com.myrealtrip.common.values.MyrealtripCommonFeature.GLOBAL_EXCEPTION_HANDLER;
import static com.myrealtrip.common.values.MyrealtripCommonFeature.HEALTH_CHECK_CONTROLLER;

@Import(MyrealtripCommonFeatureRegisterer.class)
@EnableMyrealtripCommonFeatures(features = { HEALTH_CHECK_CONTROLLER, GLOBAL_EXCEPTION_HANDLER})
@SpringBootApplication(scanBasePackages = { "com.myrealtrip"})
@EnableScheduling
public class ApiApplication {

    public static void main(String [] args) {
        try {
            ApplicationContext ctx = SpringApplication.run(ApiApplication.class, args);

            DispatcherServlet dispatcherServlet = (DispatcherServlet) ctx.getBean("dispatcherServlet");
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
            dispatcherServlet.setDispatchOptionsRequest(true);
        }catch(Exception ex) {
            System.err.println(ex.toString());
        }
    }
}