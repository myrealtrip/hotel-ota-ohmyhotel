package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhBookingDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HotelCancelCheckWriter implements ItemWriter<Reservation> {

    private final OmhBookingDetailAgent omhBookingDetailAgent;
    private final MrtReservationNoStorage mrtReservationNoStorage;

    @Override
    public void write(List<? extends Reservation> list) throws Exception {
        for (Reservation reservation : list) {
            hotelCancelCheck(reservation);
        }
    }

    public void hotelCancelCheck(Reservation reservation) {
        if (reservation.getReservationStatus() != ReservationStatus.RESERVE_CONFIRM) {
            return;
        }
        OmhBookingDetailResponse response = omhBookingDetailAgent.bookingDetail(reservation.getMrtReservationNo());
        if (response.getStatus() == OmhBookingStatus.CANCELLED) {
            mrtReservationNoStorage.addAll(List.of(reservation.getMrtReservationNo()));
        }
    }
}
