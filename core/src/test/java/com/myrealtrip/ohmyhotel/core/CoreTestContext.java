package com.myrealtrip.ohmyhotel.core;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootConfiguration
@ComponentScan(value = {"com.myrealtrip.ohmyhotel", "com.myrealtrip.srtcommon"})
@EnableAutoConfiguration
public class CoreTestContext {
}
