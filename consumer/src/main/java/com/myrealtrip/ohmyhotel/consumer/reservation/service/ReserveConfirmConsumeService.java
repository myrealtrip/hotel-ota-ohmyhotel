package com.myrealtrip.ohmyhotel.consumer.reservation.service;

import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReserveConfirmConsumeService {

    private final ReservationProvider reservationProvider;
}
