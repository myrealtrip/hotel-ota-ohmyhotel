package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.ReservationApiLog;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationApiLogEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.ReservationApiLogRepository;
import com.myrealtrip.ohmyhotel.core.provider.reservation.mapper.ReservationApiLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationApiLogProvider {

    private final ReservationApiLogRepository reservationApiLogRepository;
    private final ReservationApiLogMapper reservationApiLogMapper;

    public void upsert(ReservationApiLog reservationApiLog) {
        ReservationApiLogEntity entity = reservationApiLogMapper.toEntity(reservationApiLog);
        reservationApiLogRepository.save(entity);
    }
}
