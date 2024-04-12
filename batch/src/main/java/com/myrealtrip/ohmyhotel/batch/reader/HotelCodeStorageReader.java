package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class HotelCodeStorageReader extends AbstractPagingItemReader<Long> {

    private final HotelCodeStorage updatedHotelCodeStorage;

    public HotelCodeStorageReader(HotelCodeStorage updatedHotelCodeStorage,
                                  int pageSize) {
        this.updatedHotelCodeStorage = updatedHotelCodeStorage;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        log.info("page {} read", super.getPage());
        super.results = updatedHotelCodeStorage.getHotelCodes().stream()
            .skip((long) super.getPage() * super.getPageSize())
            .limit(getPageSize())
            .collect(Collectors.toList());
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}