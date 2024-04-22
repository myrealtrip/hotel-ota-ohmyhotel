package com.myrealtrip.ohmyhotel.core.config.cache.annotation;

import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.GlobalCache;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalCachePut {

    GlobalCache cache();

    String param() default StringUtils.EMPTY;

    Class<?> type() default Object.class;
}
