package com.myrealtrip.ohmyhotel.configuration.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.MasterSlaveServersConfig;
import org.redisson.config.ReadMode;
import org.redisson.config.TransportMode;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.List;

@Configuration
@EnableRedisRepositories("com.myrealtrip.*")
@EnableConfigurationProperties({ RedissonSpringDataConfiguration.RedisServerConfig.class})
@RequiredArgsConstructor
public class RedissonSpringDataConfiguration {
    @Value("${myrealtrip.redis.threads}")
    private int threads;
    @Value("${myrealtrip.redis.nettyThreads}")
    private int nettyThreads;
    @Value("${myrealtrip.redis.transportMode}")
    private String transportMode;
    private final RedisServerConfig redisServerConfig;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setThreads(threads);
        config.setCodec(StringCodec.INSTANCE);
        config.setNettyThreads(nettyThreads);
        config.setTransportMode(getTransportMode(transportMode));
        // https://github.com/redisson/redisson/wiki/2.-Configuration/#28-master-slave-mode config 참고
        MasterSlaveServersConfig masterSlaveServersConfig = config.useMasterSlaveServers();
        masterSlaveServersConfig.setMasterAddress(redisServerConfig.getMasterAddress());
        redisServerConfig.getReplicaAddresses().forEach(masterSlaveServersConfig::addSlaveAddress);
        masterSlaveServersConfig
            .setReadMode(ReadMode.SLAVE)
            .setIdleConnectionTimeout(redisServerConfig.getIdleConnectionTimeout())
            .setIdleConnectionTimeout(redisServerConfig.getIdleConnectionTimeout())
            .setConnectTimeout(redisServerConfig.getConnectTimeout())
            .setTimeout(redisServerConfig.getTimeout())
            .setRetryAttempts(redisServerConfig.getRetryAttempts())
            .setRetryInterval(redisServerConfig.getRetryInterval())
            .setSubscriptionsPerConnection(redisServerConfig.getSubscriptionsPerConnection())
            .setSubscriptionConnectionMinimumIdleSize(redisServerConfig.getSubscriptionConnectionMinimumIdleSize())
            .setSubscriptionConnectionPoolSize(redisServerConfig.getSubscriptionConnectionPoolSize())
            .setMasterConnectionMinimumIdleSize(redisServerConfig.getMasterConnectionMinimumIdleSize())
            .setSlaveConnectionMinimumIdleSize(redisServerConfig.getSlaveConnectionMinimumIdleSize())
            .setMasterConnectionPoolSize(redisServerConfig.getMasterConnectionPoolSize())
            .setSlaveConnectionPoolSize(redisServerConfig.getSlaveConnectionPoolSize())
            .setDatabase(redisServerConfig.getDatabase())
            .setDnsMonitoringInterval(redisServerConfig.getDnsMonitoringInterval());
        return Redisson.create(config);
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient client) {
        return new RedissonConnectionFactory(client);
    }

    private TransportMode getTransportMode(String transportMode) {
        if ("NIO".equalsIgnoreCase(transportMode)) {
            return TransportMode.NIO;
        }
        if ("EPOLL".equalsIgnoreCase(transportMode)) {
            return TransportMode.EPOLL;
        }
        if ("KQUEUE".equalsIgnoreCase(transportMode)) {
            return TransportMode.KQUEUE;
        }

        return TransportMode.NIO;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "myrealtrip.redis.redis-server-config")
    public static class RedisServerConfig {
        private String masterAddress;
        private List<String> replicaAddresses;
        private int idleConnectionTimeout;
        private int connectTimeout;
        private int timeout;
        private int retryAttempts;
        private int retryInterval;
        private int subscriptionsPerConnection;
        private int subscriptionConnectionMinimumIdleSize;
        private int subscriptionConnectionPoolSize;
        private int masterConnectionMinimumIdleSize;
        private int slaveConnectionMinimumIdleSize;
        private int masterConnectionPoolSize;
        private int slaveConnectionPoolSize;
        private int database;
        private int dnsMonitoringInterval;
    }
}