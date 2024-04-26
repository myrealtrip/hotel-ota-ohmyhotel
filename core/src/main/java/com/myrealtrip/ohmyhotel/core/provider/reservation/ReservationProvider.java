package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.OrderFormInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.ReservationRepository;
import com.myrealtrip.ohmyhotel.core.provider.reservation.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReservationProvider {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public void upsert(Reservation reservation) {
        ReservationEntity entity = reservationMapper.toEntity(reservation);
        reservationRepository.save(entity);
    }

    @Transactional
    public Reservation getByMrtReservationNoWithLock(String mrtReservationNo) {
        return reservationMapper.toDto(reservationRepository.findByMrtReservationNoWithLock(mrtReservationNo));
    }

    @Transactional
    public Reservation getByMrtReservationNo(String mrtReservationNo) {
        return reservationMapper.toDto(reservationRepository.findByMrtReservationNo(mrtReservationNo));
    }

    @Transactional
    public Reservation updateOrderFormInfo(Long reservationId, OrderFormInfo orderFormInfo) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.updateOrderFormInfo(orderFormInfo);
        return reservationMapper.toDto(reservationRepository.save(entity));
    }

    @Transactional
    public void confirm(Long reservationId, String omhBookCode, String hotelConfirmNo) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.confirm(omhBookCode, hotelConfirmNo);
        reservationRepository.save(entity);
    }

    @Transactional
    public void confirmPending(Long reservationId, String omhBookCode, String hotelConfirmNo) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.confirmPending(omhBookCode, hotelConfirmNo);
        reservationRepository.save(entity);
    }

    @Transactional
    public void confirmFail(Long reservationId, String bookintErrorCode) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.confirmFail(bookintErrorCode);
        reservationRepository.save(entity);
    }

    private ReservationEntity findByReservationId(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(IllegalArgumentException::new);
    }
}
