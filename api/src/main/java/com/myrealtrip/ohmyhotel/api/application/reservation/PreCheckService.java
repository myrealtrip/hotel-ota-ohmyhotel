package com.myrealtrip.ohmyhotel.api.application.reservation;

import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.PreCheckRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.PreCheckResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.ReservationConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.OrderProvider;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhPreCheckAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.constant.booking.PreCheckStatus;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.precheck.response.PreCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreCheckService {

    private final ReservationApiLogService reservationApiLogService;
    private final OrderProvider orderProvider;
    private final ReservationProvider reservationProvider;
    private final OmhPreCheckAgent omhPreCheckAgent;
    private final PreCheckRequestConverter preCheckRequestConverter;
    private final PreCheckResponseConverter preCheckResponseConverter;
    private final ReservationConverter reservationConverter;

    @Transactional
    public PreCheckResponse preCheck(PreCheckRequest preCheckRequest) {
        String mrtReservationNo = preCheckRequest.getMrtOrderNumber();
        Order order = orderProvider.getByOrderId(Long.valueOf(preCheckRequest.getPreCheckApiKey()));

        OmhPreCheckRequest omhPreCheckRequest = preCheckRequestConverter.toOmhPreCheckRequest(preCheckRequest, order.getAdditionalInfo().getRateType(), order.getDepositPrice());
        OmhPreCheckResponse omhPreCheckResponse = omhPreCheckAgent.preCheck(omhPreCheckRequest);
        saveApiLog(mrtReservationNo, omhPreCheckRequest, omhPreCheckResponse);

        PreCheckResponse preCheckResponse = preCheckResponseConverter.toPreCheckResponse(omhPreCheckResponse, order);
        Reservation reservation = reservationProvider.getByMrtReservationNo(mrtReservationNo);
        if (isNull(reservation)) {
            createReservation(order, mrtReservationNo, preCheckResponse.getStatus());
        } else if (reservation.getReservationStatus() == ReservationStatus.PRECHECK_FAIL) { // preCheck 에 실패한 예약번호는 다시 들어올 수 있음
            updateReservation(order, reservation.getReservationId(), mrtReservationNo, preCheckResponse.getStatus());
        } else {
            log.error("cannot preCheck reservation - {}", mrtReservationNo);
            return preCheckResponseConverter.toErrorPreCheckResponse();
        }
        return preCheckResponse;
    }

    private void saveApiLog(String mrtReservationNo,
                            OmhPreCheckRequest omhPreCheckRequest,
                            OmhPreCheckResponse omhPreCheckResponse) {
        reservationApiLogService.savePreCheckLog(mrtReservationNo, ApiLogType.REQUEST, ObjectMapperUtils.writeAsString(omhPreCheckRequest));
        reservationApiLogService.savePreCheckLog(mrtReservationNo, ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhPreCheckResponse));
    }

    private void createReservation(Order order, String mrtReservationNo, PreCheckStatus preCheckStatus) {
        Reservation reservation = reservationConverter.toReservation(order, mrtReservationNo, preCheckStatus);
        reservationProvider.upsert(reservation);
    }

    private void updateReservation(Order order, Long reservationId, String mrtReservationNo, PreCheckStatus preCheckStatus) {
        Reservation reservation = reservationConverter.toReservation(order, reservationId, mrtReservationNo, preCheckStatus);
        reservationProvider.upsert(reservation);
    }
}
