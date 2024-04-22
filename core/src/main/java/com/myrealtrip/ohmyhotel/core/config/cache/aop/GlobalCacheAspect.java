package com.myrealtrip.ohmyhotel.core.config.cache.aop;

import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.GlobalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheEvict;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCachePut;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheable;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.Objects;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class GlobalCacheAspect implements CacheAspectHandleable {

    private final String environment;
    private final RedissonClient redissonClient;

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheable)")
    public Object handleCacheable(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        GlobalCacheable globalCacheable = methodSignature.getMethod().getAnnotation(GlobalCacheable.class);
        String cacheKey = globalCacheable.cache().generateKey(environment, parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), globalCacheable.param()));
        log.debug(">>> [GlobalCacheAspect] handleCacheable {}", cacheKey);

        RBucket<String> bucket;
        try {
            bucket = redissonClient.getBucket(cacheKey);
        } catch (Exception e) {
            log.error(">>> [GlobalCacheAspect] Failed to retrieve cache.", e);
            return proceedingJoinPoint.proceed();
        }

        Object result;
        if (!bucket.isExists()) {
            result = proceedingJoinPoint.proceed();
            put(result, globalCacheable.cache(), bucket);
        } else {
            result = ObjectMapperUtils.readString(StringUtils.defaultString(bucket.get(), StringUtils.EMPTY), globalCacheable.type());
        }
        return result;
    }

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCachePut)")
    public Object handleCachePut(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        GlobalCachePut globalCachePut = methodSignature.getMethod().getAnnotation(GlobalCachePut.class);
        String cacheKey = globalCachePut.cache().generateKey(environment, parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), globalCachePut.param()));
        log.debug(">>> [GlobalCacheAspect] handleCachePut {}", cacheKey);
        RBucket<String> bucket;
        try {
            bucket = redissonClient.getBucket(cacheKey);
        } catch (Exception e) {
            log.error(">>> [GlobalCacheAspect] Failed to retrieve cache.", e);
            return proceedingJoinPoint.proceed();
        }

        Object result = proceedingJoinPoint.proceed();
        put(result, globalCachePut.cache(), bucket);
        return result;
    }

    @Override
    @Around("@annotation(com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheEvict)")
    public Object handleCacheEvict(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
        GlobalCacheEvict globalCacheEvict = methodSignature.getMethod().getAnnotation(GlobalCacheEvict.class);
        String cacheKey = globalCacheEvict.cache().generateKey(environment, parseParam(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs(), globalCacheEvict.param()));
        log.info(">>> [GlobalCacheAspect] handleCacheEvict {}", cacheKey);
        RBucket<String> bucket;
        try {
            bucket = redissonClient.getBucket(cacheKey);
        } catch (Exception e) {
            log.error(">>> [GlobalCacheAspect] Failed to retrieve cache.", e);
            return proceedingJoinPoint.proceed();
        }
        Object result = proceedingJoinPoint.proceed();
        bucket.delete();
        return result;
    }

    private void put(Object result, GlobalCache cache, RBucket<String> bucket) {
        try {
            if (result != null) {
                String json = ObjectMapperUtils.writeAsString(result);
                if (Objects.nonNull(cache.getTtl()) && 0 < cache.getTtl()) {
                    bucket.setAsync(json, cache.getTtl(), cache.getTtlUnit());
                } else {
                    bucket.setAsync(json);
                }
            }
        } catch (Exception e) {
            log.error(">>> [GlobalCacheAspect] Failed to put.", e);
        }
    }
}
