package com.myrealtrip.ohmyhotel.api.presentation.reservation;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.reservation.CancelRefundCalculateService;
import com.myrealtrip.ohmyhotel.api.application.reservation.OrderSearchService;
import com.myrealtrip.ohmyhotel.api.application.reservation.PreCheckService;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.booking.response.ItineraryCancelRefundResponse;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.precheck.response.PreCheckResponse;
import com.myrealtrip.unionstay.dto.hotelota.search.request.ReservationSearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final OrderSearchService orderSearchService;
    private final PreCheckService preCheckService;
    private final CancelRefundCalculateService cancelRefundCalculateService;

    @Operation(summary = "실시간 재고 및 가격 조회 (주문서 조회 용도)", description = "property 의 실시간 재고와 가격을 검색합니다. (주문서 조회 용도)")
    @GetMapping(value = "/v2/properties/search/reservation")
    public Resource<SearchResponse> searchForOrder(@Valid ReservationSearchRequest searchRequest) {
        log.info("{}", ObjectMapperUtils.writeAsString(searchRequest));
        SearchResponse searchResponse = orderSearchService.search(searchRequest);
        log.info("{}", ObjectMapperUtils.writeAsString(searchResponse));
        return Resource.<SearchResponse>builder()
            .data(searchResponse)
            .build();
    }

    @Operation(summary = "가격/재고 최종 확인", description = "결제 전 최종적으로 가격/재고를 확인합니다.")
    @PostMapping("/properties/pre-check")
    public Resource<PreCheckResponse> availability(@RequestBody PreCheckRequest preCheckRequest) {
        log.info("preCheckRequest: {}", ObjectMapperUtils.writeAsString(preCheckRequest));
        PreCheckResponse preCheckResponse = preCheckService.preCheck(preCheckRequest);
        log.info("preCheckResponse: {}", ObjectMapperUtils.writeAsString(preCheckResponse));
        return Resource.<PreCheckResponse>builder()
            .data(preCheckResponse)
            .build();
    }

    @Operation(summary = "실시간 취소 환불 금액 조회", description = "해당 예약에 대해 실시간으로 OTA의 취소 환불 금액을 받아온다.")
    @GetMapping("/itineraries/{mrtReservationNo}/cancel_refunds")
    public Resource<ItineraryCancelRefundResponse> cancelRefunds(@PathVariable("mrtReservationNo") String mrtReservationNo) {
        ItineraryCancelRefundResponse response  = cancelRefundCalculateService.getCancelRefund(mrtReservationNo);
        return Resource.<ItineraryCancelRefundResponse>builder()
            .data(response)
            .build();
    }
}
