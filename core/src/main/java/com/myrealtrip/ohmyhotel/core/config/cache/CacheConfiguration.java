package com.myrealtrip.ohmyhotel.core.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.CacheName;
import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.LocalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.aop.GlobalCacheAspect;
import com.myrealtrip.ohmyhotel.core.config.cache.aop.LocalCacheAspect;
import com.myrealtrip.unionstay.common.exception.UnionStayBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheConfiguration {

    private static final int LOCAL_ASPECT_ORDER = 100;
    private static final int GLOBAL_ASPECT_ORDER = 200;
    private final RedissonClient redissonClient;

    @Value("${spring.profiles.active}")
    private String profile;

    @PostConstruct
    public void initCacheName() {
        long distinctCount = Arrays.stream(CacheName.values()).map(CacheName::getCacheKeyType).distinct().count();
        if (CacheName.values().length != distinctCount) {
            throw new UnionStayBaseException("중복 캐시키가 있습니다");
        }
    }

    @Bean
    public CacheManager localCacheManager() {
        List<CaffeineCache> caches = Arrays.stream(LocalCache.values())
            .map(cache -> new CaffeineCache(cache.getCacheName().getCacheKeyType(),
                                            Caffeine.newBuilder()
                                                .recordStats()
                                                .expireAfterWrite(cache.getTtl(), cache.getTtlUnit())
                                                .build()
                 )
            )
            .collect(Collectors.toList());
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caches);
        return simpleCacheManager;
    }

    @Bean
    @Order(value = LOCAL_ASPECT_ORDER)
    public LocalCacheAspect localCacheAspect() {
        return new LocalCacheAspect(localCacheManager());
    }

    @Bean
    @Order(value = GLOBAL_ASPECT_ORDER)
    public GlobalCacheAspect globalCacheAspect() {
        return new GlobalCacheAspect(profile, redissonClient);
    }
}
