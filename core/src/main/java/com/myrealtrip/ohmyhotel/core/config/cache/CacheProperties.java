package com.myrealtrip.ohmyhotel.core.config.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class CacheProperties {
    private static final String DOMAIN_NAME = "ohmyhotel";

    @Getter
    @AllArgsConstructor
    public enum CacheName {
        ;

        private final String cacheKeyType;
    }

    @Getter
    @AllArgsConstructor
    public enum GlobalCache {
        ;

        private static final String GLOBAL_CACHE_KEY_FORMAT = "%s:%s:%s:%s";    // ex. unionstay:{environment}:{cacheName}:{key}

        private final CacheName cacheName;
        private final Long ttl;
        private final TimeUnit ttlUnit;

        public String generateKey(String environment, String param) {
            return String.format(GLOBAL_CACHE_KEY_FORMAT, DOMAIN_NAME, environment, cacheName.getCacheKeyType(), param);
        }

        public long giveTtlInMinutes() {
            if (ttl == null || ttlUnit == null) {
                return 0;
            }

            switch (ttlUnit) {
                case DAYS:
                    return ttl * 24 * 60;
                case HOURS:
                    return ttl * 60;
                case MINUTES:
                    return ttl;
                case SECONDS:
                    return ttl / 60;
                case MILLISECONDS:
                    return ttl / 60 / 1000;
                default:
                    return ttl;
            }
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