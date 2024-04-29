package com.myrealtrip.ohmyhotel.api.presentation.reservation;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingMessageKafkaSendService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReservationAdminController {

    private final BookingMessageKafkaSendService bookingMessageKafkaSendService;

    @Operation(summary = "예약 상태 강제변경 API", description = "에약의 상태값을 강제로 변경한다.")
    @PostMapping(value = "/admin/reservation/{mrtReservationNo}/force-status-update/{status}")
    public Resource<Void> sendReservation(@PathVariable("mrtReservationNo") String mrtReservationNo,
                                          @PathVariable("status") ReservationStatus status) {
        bookingMessageKafkaSendService.sendByMrtReservationNo(mrtReservationNo, null, 0);
        return Resource.<Void>builder()
            .data(null)
            .build();
    }

    @Operation(summary = "예약결과 메세지 재발행 API", description = "booking-detail-upsert 메세지를 재발행한다.")
    @GetMapping(value = "/admin/reservation/{mrtReservationNo}/re-publish")
    public Resource<Void> sendReservation(@PathVariable("mrtReservationNo") String mrtReservationNo) {
        bookingMessageKafkaSendService.sendByMrtReservationNo(mrtReservationNo, null, 0);
        return Resource.<Void>builder()
            .data(null)
            .build();
    }
}
