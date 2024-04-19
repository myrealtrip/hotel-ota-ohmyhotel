package com.myrealtrip.ohmyhotel.api.application.meta;

import com.myrealtrip.ohmyhotel.api.application.meta.converter.RoomMetaRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.meta.converter.RoomMetaResponseConverter;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomInfoAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomInfoResponse;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaRequest;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomMetaService {

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(200);

    private final OmhRoomInfoAgent omhRoomInfoAgent;
    private final RoomMetaRequestConverter roomMetaRequestConverter;
    private final RoomMetaResponseConverter roomMetaResponseConverter;

    public List<RoomMetaResponse> getRoomMetas(RoomMetaRequest roomMetaRequest) {
        return Flux.fromIterable(roomMetaRequestConverter.toOmhRoomInfoRequests(roomMetaRequest))
            .parallel(20)
            .runOn(Schedulers.fromExecutorService(fixedThreadPool))
            .map(omhRoomInfoRequest -> {
                try {
                    Thread.sleep(3000);
                    return omhRoomInfoAgent.getRoomInfo(omhRoomInfoRequest);
                } catch (Throwable t) {
                    log.error("Room Info API Error", t);
                    return OmhRoomInfoResponse.builder().hotelCode(null).build();
                }
            })
            .filter(omhRoomInfoResponse -> !omhRoomInfoResponse.isEmpty())
            .map(roomMetaResponseConverter::toRoomMetaResponse)
            .sequential()
            .collectList()
            .block();
    }
}
