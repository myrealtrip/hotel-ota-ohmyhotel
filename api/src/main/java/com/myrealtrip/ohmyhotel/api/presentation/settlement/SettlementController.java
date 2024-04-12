package com.myrealtrip.ohmyhotel.api.presentation.settlement;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.settlement.SettlementService;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigGetRequest;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigRequest;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(summary = "숙소별 수수료 설정 API")
    @PostMapping("/settlement/commissionRate")
    public Resource<String> updateSettlementConfig(@RequestBody SettlementConfigRequest request) {
        settlementService.upsert(request);
        return Resource.<String>builder()
            .data("OK")
            .build();
    }

    @Operation(summary = "숙소 수수료 조회 API")
    @GetMapping("/settlement/commissionRate")
    public Resource<SettlementConfigResponse> getSettlementConfig(SettlementConfigGetRequest request) {
        SettlementConfigResponse response = settlementService.getSettlementConfig(request);
        return Resource.<SettlementConfigResponse>builder()
            .data(response)
            .build();
    }
}
