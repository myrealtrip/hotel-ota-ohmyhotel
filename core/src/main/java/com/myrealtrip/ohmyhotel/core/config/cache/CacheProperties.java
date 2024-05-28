package com.myrealtrip.ohmyhotel.core.config.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class CacheProperties {
    private static final String DOMAIN_NAME = "ohmyhotel";

    @Getter
    @AllArgsConstructor
    public enum CacheName {
        ROOM_META("room_meta"),
        HOTELS_AVAILABILITY("multiple_availability"),
        ;

        private final String cacheKeyType;
    }

    @Getter
    @AllArgsConstructor
    public enum GlobalCache {
        ROOM_META(CacheName.ROOM_META, 6L, TimeUnit.HOURS),
        HOTELS_AVAILABILITY(CacheName.HOTELS_AVAILABILITY, 10L, TimeUnit.MINUTES),
        ;

        private static final String GLOBAL_CACHE_KEY_FORMAT = "%s:%s:%s:%s";    // ex. unionstay:{environment}:{cacheName}:{key}

        private final CacheName cacheName;
        private final Long ttl;
        private final TimeUnit ttlUnit;

        public String generateKey(String environment, String param) {
            return String.format(GLOBAL_CACHE_KEY_FORMAT, DOMAIN_NAME, environment, cacheName.getCacheKeyType(), param);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum LocalCache {
        ;

        private static final String LOCAL_CACHE_KEY_FORMAT = "%s:%s:%s";  // ex. unionstay:{cacheName}:{key}

        private final CacheName cacheName;
        private final Long ttl;
        private final TimeUnit ttlUnit;

        public String generateKey(String param) {
            return String.format(LOCAL_CACHE_KEY_FORMAT, DOMAIN_NAME, cacheName.getCacheKeyType(), param);
        }
    }
}