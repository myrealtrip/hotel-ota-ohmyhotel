package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.AbstractPagingItemReader;

/**
 * DB 에서 호텔 데이터를 가져온다.
 */
@Slf4j
public class HotelReader extends AbstractPagingItemReader<Hotel> {

    private final HotelProvider hotelProvider;
    private Long lastSelectId = 0L;

    public HotelReader(HotelProvider hotelProvider, int pageSize) {
        this.hotelProvider = hotelProvider;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        log.info("page {}, read", super.getPage());

        super.results = hotelProvider.getByHotelIdGreaterThan(lastSelectId, getPageSize());
        if (CollectionUtils.isNotEmpty(super.results)) {
            lastSelectId = super.results.get(super.results.size() - 1).getHotelId();
        }
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
