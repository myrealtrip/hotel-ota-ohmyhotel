package com.myrealtrip.ohmyhotel.api.presentation.reservation;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.reservation.OrderSearchService;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final OrderSearchService orderSearchService;

    @Operation(summary = "실시간 재고 및 가격 조회 (주문서 조회 용도)", description = "property 의 실시간 재고와 가격을 검색합니다. (주문서 조회 용도)")
    @GetMapping(value = "/properties/search/reservation")
    public Resource<SearchResponse> searchForOrder(@Valid SearchRequest searchRequest) {
        log.info("{}", ObjectMapperUtils.writeAsString(searchRequest));
        SearchResponse searchResponse = orderSearchService.search(searchRequest);
        log.info("{}", ObjectMapperUtils.writeAsString(searchResponse));
        return Resource.<SearchResponse>builder()
            .data(searchResponse)
            .build();
    }
}
