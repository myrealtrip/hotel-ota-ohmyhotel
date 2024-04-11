package com.myrealtrip.ohmyhotel.api.presentation.settlement;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.settlement.SettlementService;
import com.myrealtrip.unionstay.dto.hotelota.settlementconfig.SettlementConfigRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
