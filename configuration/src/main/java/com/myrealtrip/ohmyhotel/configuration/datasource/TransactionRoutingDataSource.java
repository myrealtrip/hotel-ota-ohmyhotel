package com.myrealtrip.ohmyhotel.configuration.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    private final Environment environment;

    @Override
    protected Object determineCurrentLookupKey() {
        if (isBatch() || TransactionSynchronizationManager.isCurrentTransactionReadOnly() == false) {
            return TransactionType.READ_WRITE;
        }
        return TransactionType.READ_ONLY;
    }

    private boolean isBatch() {
        return Arrays.asList(environment.getActiveProfiles()).contains("batch");
    }
}
