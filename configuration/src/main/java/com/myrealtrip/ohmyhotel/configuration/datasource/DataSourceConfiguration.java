package com.myrealtrip.ohmyhotel.configuration.datasource;

import com.myrealtrip.cipher.CipherDecryptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@Slf4j
public class DataSourceConfiguration {
    @Value("${spring.config.activate.on-profile}")
    private String profile;

    @Value("${myrealtrip.datasource.read-write.jdbc-url}")
    private String readWriteJdbcUrl;

    @Value("${myrealtrip.datasource.read-write.username}")
    private String readWriteDbUser;

    @Value("${myrealtrip.datasource.read-write.password}")
    private String readWriteDbPassword;

    @Value("${myrealtrip.datasource.read-only.jdbc-url}")
    private String readOnlyJdbcUrl;

    @Value("${myrealtrip.datasource.read-only.username}")
    private String readOnlyDbUser;

    @Value("${myrealtrip.datasource.read-only.password}")
    private String readOnlyDbPassword;

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "myrealtrip.datasource.read-write.extra")
    public HikariDataSource readWriteDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .url(CipherDecryptor.decrypt(profile, readWriteJdbcUrl))
            .username(CipherDecryptor.decrypt(profile, readWriteDbUser))
            .password(CipherDecryptor.decrypt(profile, readWriteDbPassword))
            .build();
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "myrealtrip.datasource.read-only.extra")
    public HikariDataSource readOnlyDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .url(CipherDecryptor.decrypt(profile, readOnlyJdbcUrl))
            .username(CipherDecryptor.decrypt(profile, readOnlyDbUser))
            .password(CipherDecryptor.decrypt(profile, readOnlyDbPassword))
            .build();
    }

    @Bean
    public TransactionRoutingDataSource routingDataSource(@Qualifier("readWriteDataSource") DataSource readWriteDataSource,
                                                          @Qualifier("readOnlyDataSource") DataSource readOnlyDataSource,
                                                          Environment environment) {
        TransactionRoutingDataSource transactionRoutingDataSource = new TransactionRoutingDataSource(environment);

        Map<Object, Object> dataSourceMap = new HashMap<>();

        dataSourceMap.put(TransactionType.READ_ONLY, readOnlyDataSource);
        dataSourceMap.put(TransactionType.READ_WRITE, readWriteDataSource);

        transactionRoutingDataSource.setTargetDataSources(dataSourceMap);

        return transactionRoutingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(TransactionRoutingDataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }
}
