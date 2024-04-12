package com.myrealtrip.ohmyhotel.batch.listener;

import com.myrealtrip.ohmyhotel.batch.service.PropertyUpsertKafkaSendService;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.util.List;

/**
 * 호텔 업데이트 청크가 완료되면 kafka 메세지를 전송한다.
 */
@RequiredArgsConstructor
public class HotelUpdateChunkListener implements ChunkListener {

    private final HotelCodeStorage chunkUpdatedHotelCodeStorage;
    private final PropertyUpsertKafkaSendService propertyUpsertKafkaSendService;

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    public void afterChunk(ChunkContext context) {
//        propertyUpsertKafkaSendService.sendByHotelIds(chunkUpdatedHotelCodeStorage.getHotelCodes());
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
