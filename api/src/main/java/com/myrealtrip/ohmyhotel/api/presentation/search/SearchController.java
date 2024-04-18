package com.myrealtrip.ohmyhotel.api.presentation.search;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.search.SearchService;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "실시간 재고 및 가격 조회", description = "property 의 실시간 재고와 가격을 검색합니다.")
    @GetMapping(value = "/properties/search")
    public Resource<SearchResponse> search(@Valid SearchRequest searchRequest) {
        SearchResponse searchResponse = searchService.search(searchRequest);
        return Resource.<SearchResponse>builder()
            .data(searchResponse)
            .build();
    }
}