package com.myrealtrip.ohmyhotel.api.presentation.meta;

import com.myrealtrip.common.values.Resource;
import com.myrealtrip.ohmyhotel.api.application.meta.RoomMetaService;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaRequest;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaResponse;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MetaController {

    private final RoomMetaService roomMetaService;

    @Operation(summary = "객실 메타정보 조회", description = "객실 메타정보를 조회합니다.")
    @PostMapping(value = "/meta/room")
    public Resource<List<RoomMetaResponse>> search(@RequestBody RoomMetaRequest roomMetaRequest) {
        List<RoomMetaResponse> roomMetaResponses = roomMetaService.getRoomMetas(roomMetaRequest);
        return Resource.<List<RoomMetaResponse>>builder()
            .data(roomMetaResponses)
            .build();
    }
}
