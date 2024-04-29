package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.producer.CommonProducer;
import com.myrealtrip.unionstay.common.message.booking.UpsertBookingDetailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingMessageKafkaSendService {

    private final ReservationProvider reservationProvider;
    private final CommonProducer producer;
    private final UpsertBookingDetailMessageConverter messageConverter;

    /**
     * 통합숙소로 예약 upsert 메세지를 전달한다.
     * @param mrtReservationNo 마리트 예약번호
     * @param targetStatusList 현재 예약의 상태가 해당 상태값과 일치할때만 메세지를 publish 함. null 이라면 무시
     * @param retryCount retry 횟수
     */
    @Transactional
    public void sendByMrtReservationNo(String mrtReservationNo, List<ReservationStatus> targetStatusList, int retryCount) {
        Reservation reservation = reservationProvider.getByMrtReservationNo(mrtReservationNo);
        if (nonNull(targetStatusList) && !targetStatusList.contains(reservation.getReservationStatus())) {
            return;
        }
        UpsertBookingDetailMessage message = messageConverter.toMessage(reservation, retryCount);
        producer.publishUpsertBookingDetail(message);
    }
}
