package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.stream.Collectors;

@Slf4j
public class HotelCodeStorageReader extends AbstractPagingItemReader<Long> {

    private final HotelCodeStorage allHotelCodeStorage;

    public HotelCodeStorageReader(HotelCodeStorage allHotelCodeStorage,
                                  int pageSize) {
        this.allHotelCodeStorage = allHotelCodeStorage;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        log.info("page {} read", super.getPage());
        super.results = allHotelCodeStorage.getHotelCodes().stream()
            .skip((long) super.getPage() * super.getPageSize())
            .limit(getPageSize())
            .collect(Collectors.toList());
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
