package com.myrealtrip.ohmyhotel.api.application.common.converter;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuestCount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.unionstay.common.constant.RateOption;
import com.myrealtrip.unionstay.common.constant.SalesEnvironment;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class SearchRequestConverter {

    public OmhHotelsAvailabilityRequest toOmhHotelsAvailabilityRequest(SearchRequest searchRequest, List<Long> hotelCodes) {
        return OmhHotelsAvailabilityRequest.builder()
            .nationalityCode(searchRequest.getCountryCode().getIso2CountryCode())
            .language(Language.KO)
            .checkInDate(searchRequest.getCheckin())
            .checkOutDate(searchRequest.getCheckout())
            .rooms(List.of(toOmhRoomGuestCount(searchRequest)))
            .rateType(toRateType(searchRequest))
            .hotelCodes(hotelCodes)
            .build();
    }

    public OmhRoomsAvailabilityRequest toOmhRoomsAvailabilityRequest(SearchRequest searchRequest) {
        return OmhRoomsAvailabilityRequest.builder()
            .nationalityCode(searchRequest.getCountryCode().getIso2CountryCode())
            .language(Language.KO)
            .checkInDate(searchRequest.getCheckin())
            .checkOutDate(searchRequest.getCheckout())
            .rooms(List.of(toOmhRoomGuestCount(searchRequest)))
            .rateType(toRateType(searchRequest))
            .hotelCode(Long.valueOf(searchRequest.getPropertyIds().get(0)))
            .build();
    }

    /* https://myrealtrip.slack.com/archives/C051MGEQ2PP/p1716537289178509 */
    private RateType toRateType(SearchRequest searchRequest) {
        if (nonNull(searchRequest.getRateOptions()) &&
            searchRequest.getRateOptions().contains(RateOption.MEMBER)) {
            return RateType.PACKAGE_RATE;
        }
        return RateType.STANDARD_RATE;
    }

    private OmhRoomGuestCount toOmhRoomGuestCount(SearchRequest searchRequest) {
        return OmhRoomGuestCount.builder()
            .adultCount(searchRequest.getAdultCount())
            .childCount(searchRequest.getChildCount())
            .childAges(searchRequest.getChildAges())
            .build();
    }
}
