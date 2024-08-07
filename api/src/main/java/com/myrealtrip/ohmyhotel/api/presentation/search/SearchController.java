package com.myrealtrip.ohmyhotel.api.presentation.search;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.reservation.OrderSearchService;
import com.myrealtrip.ohmyhotel.api.application.search.SearchService;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "실시간 재고 및 가격 조회", description = "property 의 실시간 재고와 가격을 검색합니다.")
    @PostMapping(value = "/v2/properties/search")
    public Resource<SearchResponse> searchV2(@RequestBody SearchRequest searchRequest) {
        try {
            return Resource.<SearchResponse>builder()
                .data(searchService.search(searchRequest))
                .build();
        } catch (Throwable t) {
            log.warn("search api error request: {}", ObjectMapperUtils.writeAsString(searchRequest), t);
            throw t;
        }
    }
}
