package com.myrealtrip.ohmyhotel.core.config.cache.aop;

import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.LocalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCacheEvict;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCachePut;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LocalCacheAspect implements CacheAspectHandleable {

    private final CacheManager localCacheManager;

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCacheable)")
    public Object handleCacheable(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        LocalCacheable localCacheable = methodSignature.getMethod().getAnnotation(LocalCacheable.class);
        String cacheKey = localCacheable.cache().generateKey(parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), localCacheable.param()));
        log.debug(">>> [LocalCacheAspect] handleCacheable {}", cacheKey);
        Cache cache = getCache(localCacheable.cache());
        if(cache == null) {
            log.error(">>> [LocalCacheAspect] not registered at localCacheManager. {}", localCacheable.cache());
            return proceedingJoinPoint.proceed();
        }

        Object result = cache.get(cacheKey, localCacheable.type());
        if (result == null) {
            result = proceedingJoinPoint.proceed();
            put(result, cache, cacheKey);
        }
        return result;
    }

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCachePut)")
    public Object handleCachePut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        LocalCachePut localCachePut = methodSignature.getMethod().getAnnotation(LocalCachePut.class);
        String cacheKey = localCachePut.cache().generateKey(parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), localCachePut.param()));
        log.debug(">>> [LocalCacheAspect] handleCachePut {}", cacheKey);
        Cache cache = getCache(localCachePut.cache());
        if(cache == null) {
            log.error(">>> [LocalCacheAspect] not registered at localCacheManager. {}", localCachePut.cache());
            return proceedingJoinPoint.proceed();
        }
        Object result = proceedingJoinPoint.proceed();
        put(result, cache, cacheKey);
        return result;
    }

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.LocalCacheEvict)")
    public Object handleCacheEvict(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info(">>> [LocalCacheAspect] handleCacheEvict");
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        LocalCacheEvict localCacheEvict = methodSignature.getMethod().getAnnotation(LocalCacheEvict.class);
        String cacheKey = localCacheEvict.cache().generateKey(parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), localCacheEvict.param()));
        Cache cache = getCache(localCacheEvict.cache());
        if(cache == null) {
            log.error(">>> [LocalCacheAspect] not registered at localCacheManager. {}", localCacheEvict.cache());
            return proceedingJoinPoint.proceed();
        }

        Object result = proceedingJoinPoint.proceed();
        cache.evict(cacheKey);
        return result;
    }

    private Cache getCache(LocalCache localCache) {
        return localCacheManager.getCache(localCache.getCacheName().getCacheKeyType());
    }

    private void put(Object result, Cache cache, String cacheKey) {
        if (result != null) {
            cache.put(cacheKey, result);
            log.debug(">>> [LocalCacheAspect] put.");
        }
    }
}
