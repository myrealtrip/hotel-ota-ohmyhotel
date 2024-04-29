package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.ReservationApiLog;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationApiLogProvider;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStepApi;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhCreateBookingAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhPreCheckAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationApiLogService {

    private final ReservationApiLogProvider reservationApiLogProvider;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBookingDetailApiLog(String mrtReservationNo, ApiLogType logType, String log) {
        save(mrtReservationNo, ReservationStepApi.CREATE_BOOKING, OmhBookingDetailAgent.URI, logType, log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCreateBookingApiLog(String mrtReservationNo, ApiLogType logType, String log) {
        save(mrtReservationNo, ReservationStepApi.CREATE_BOOKING, OmhCreateBookingAgent.URI, logType, log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRoomsAvailabilityLog(Long orderId, ApiLogType logType, String log) {
        save(String.valueOf(orderId), ReservationStepApi.ROOMS_AVAILABILITY, OmhRoomsAvailabilityAgent.URI, logType, log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePreCheckApiLog(String mrtReservationNo, ApiLogType logType, String log) {
        save(mrtReservationNo, ReservationStepApi.PRE_CHECK, OmhPreCheckAgent.URI, logType, log);
    }

    private void save(String logKey, ReservationStepApi api, String url, ApiLogType type, String log) {
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
