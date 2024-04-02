package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 오마이호텔에서 호텔정보를 가져온다.
 */
public class HotelInfoReader extends AbstractPagingItemReader<OmhHotelInfoAggr> {

    private final OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent;
    private final HotelCodeStorage hotelCodeStorage;

    public HotelInfoReader(OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent,
                           HotelCodeStorage hotelCodeStorage,
                           int pageSize) {
        this.omhStaticHotelInfoListAgent = omhStaticHotelInfoListAgent;
        this.hotelCodeStorage = hotelCodeStorage;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        List<Long> hotelCodes = hotelCodeStorage.getHotelCodes().stream()
            .skip((long) super.getPage() * super.getPageSize())
            .limit(getPageSize())
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(hotelCodes)) {
            super.results = Collections.emptyList();
        }

        List<OmhHotelInfo> koOmhHotelInfoList = omhStaticHotelInfoListAgent.getHotelInfo(OmhStaticHotelInfoListRequest.create(Language.KO, hotelCodes))
            .getHotels();

        Map<Long, OmhHotelInfo> enOmhHotelInfoMap = omhStaticHotelInfoListAgent.getHotelInfo(OmhStaticHotelInfoListRequest.create(Language.EN, hotelCodes))
            .getHotels()
            .stream()
            .collect(Collectors.toMap(OmhHotelInfo::getHotelCode, Function.identity()));

        super.results = koOmhHotelInfoList.stream()
            .map(koOmhHotelInfo -> new OmhHotelInfoAggr(koOmhHotelInfo, enOmhHotelInfoMap.get(koOmhHotelInfo.getHotelCode())))
            .collect(Collectors.toList());
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
