package com.myrealtrip.ohmyhotel.api.application.common;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.ReservationApiLog;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationApiLogProvider;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStepApi;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationApiLogService {

    private final ReservationApiLogProvider reservationApiLogProvider;

    /**
     * 주문서 진입시 호출했던 rooms availability api 로그를 저장한다.
     * @param orderId order id (logKey 로 사용)
     * @param logType request or response
     * @param log
     */
    public void upsertRoomsAvailabilityLog(Long orderId, ApiLogType logType, String log) {
        upsert(String.valueOf(orderId), ReservationStepApi.ROOMS_AVAILABILITY, OmhRoomsAvailabilityAgent.URI, logType, log);
    }

    public void upsert(String logKey, ReservationStepApi api, String url, ApiLogType type, String log) {
        ReservationApiLog reservationApiLog = ReservationApiLog.builder()
            .logKey(logKey)
            .api(api)
            .url(url)
            .logType(type)
            .log(log)
            .build();
        reservationApiLogProvider.upsert(reservationApiLog);
    }
}
