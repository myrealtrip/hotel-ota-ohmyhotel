package com.myrealtrip.ohmyhotel.api.application.reservation;

import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationProvider reservationProvider;

    @Transactional
    public void forceStatusUpdate(String mrtReservationNo, ReservationStatus status) {
        reservationProvider.forceStatusUpdate(mrtReservationNo, status);
    }
}
