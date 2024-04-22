package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.CoreTestContext;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.ReservationApiLog;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStepApi;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest(classes = CoreTestContext.class, properties = "spring.profiles.active=dev")
class ReservationApiLogProviderTest {

    @Autowired
    private ReservationApiLogProvider reservationApiLogProvider;

    @Test
    @Rollback(value = false)
    @Disabled
    void upsert() {
        ReservationApiLog reservationApiLog = ReservationApiLog.builder()
            .logKey("orderId")
            .api(ReservationStepApi.ROOMS_AVAILABILITY)
            .url("url")
            .logType(ApiLogType.RESPONSE)
            .log("log")
            .build();

        reservationApiLogProvider.upsert(reservationApiLog);
    }
}