package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.OrderFormInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.ReservationRepository;
import com.myrealtrip.ohmyhotel.core.provider.reservation.mapper.ReservationMapper;
import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Reservation getByMrtReservationNoReadOnly(String mrtReservationNo) {
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

    @Transactional
    public List<Reservation> getByReservationIdGtAndStatus(Long reservationId, ReservationStatus status, int limit) {
        return reservationRepository.findByReservationIdGtAndStatus(reservationId, status, limit)
            .stream()
            .map(reservationMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void addConfirmPendingRetryCount(Long reservationId) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.addConfirmPendingRetryCount();
        reservationRepository.save(entity);
    }

    @Transactional
    public void forceStatusUpdate(String mrtReservationNo, ReservationStatus status) {
        ReservationEntity entity = reservationRepository.findByMrtReservationNo(mrtReservationNo);
        entity.forceStatusUpdate(status);
        reservationRepository.save(entity);
    }

    @Transactional
    public void cancelSuccess(Long reservationId,
                              CanceledBy canceledBy,
                              String cancelReason,
                              String cancelReasonType,
                              String omhCancelConfirmNo,
                              BigDecimal cancelPenaltyDepositPrice,
                              BigDecimal cancelPenaltySalePrice) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.cancelSuccess(canceledBy, cancelReason, cancelReasonType, omhCancelConfirmNo, cancelPenaltyDepositPrice, cancelPenaltySalePrice);
        reservationRepository.save(entity);
    }

    @Transactional
    public void cancelFail(Long reservationId,
                           CanceledBy canceledBy,
                           String cancelReason,
                           String cancelReasonType,
                           String errorMessage,
                           String bookingErrorCode) {
        ReservationEntity entity = findByReservationId(reservationId);
        entity.cancelFail(canceledBy, cancelReason, cancelReasonType, errorMessage, bookingErrorCode);
        reservationRepository.save(entity);
    }

    @Transactional
    public List<Reservation> getByReservationIdGtAndCheckInDateGoeAndStatus(Long reservationId, LocalDate checkInDate, ReservationStatus status, int limit) {
        return reservationRepository.findByReservationIdGtAndCheckInDateGoeAndStatus(reservationId, checkInDate, status, limit)
            .stream()
            .map(reservationMapper::toDto)
            .collect(Collectors.toList());
    }

    private ReservationEntity findByReservationId(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(IllegalArgumentException::new);
    }
}
