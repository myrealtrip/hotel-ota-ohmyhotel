package com.myrealtrip.ohmyhotel.core.config.cache.annotation;

import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.LocalCache;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalCachePut {

    LocalCache cache();

    String param() default StringUtils.EMPTY;

    Class<?> type() default Object.class;
}
