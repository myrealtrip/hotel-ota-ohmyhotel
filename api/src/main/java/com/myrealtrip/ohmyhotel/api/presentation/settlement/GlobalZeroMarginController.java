package com.myrealtrip.ohmyhotel.api.presentation.settlement;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.settlement.GlobalZeroMarginService;
import com.myrealtrip.ohmyhotel.api.protocol.settlement.GlobalZeroMarginResponse;
import com.myrealtrip.ohmyhotel.api.protocol.settlement.GlobalZeroMarginUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GlobalZeroMarginController {

    private final GlobalZeroMarginService globalZeroMarginService;

    @Operation(summary = "글로벌 제로마진 설정 조회")
    @GetMapping("/globalZeroMargin")
    public Resource<GlobalZeroMarginResponse> switchList() {
        return Resource.<GlobalZeroMarginResponse>builder()
            .data(globalZeroMarginService.getGlobalZeroMargin())
            .build();
    }

    @Operation(summary = "글로벌 제로마진 설정 업데이트")
    @PostMapping("/globalZeroMargin")
    public Resource<Void> updateSwitch(@RequestBody GlobalZeroMarginUpsertRequest globalZeroMarginUpsertRequest) {
        globalZeroMarginService.upsertGlobalZeroMargin(globalZeroMarginUpsertRequest);
        globalZeroMarginService.callUnionstayZeroMarginSwitch();
        return Resource.<Void>builder()
            .data(null)
            .build();
    }
}
