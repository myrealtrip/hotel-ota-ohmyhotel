package com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.AgentTestContext;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest(classes = AgentTestContext.class, properties = "spring.profiles.active=dev")
@Slf4j
class OmhStaticHotelInfoListAgentTest {

    @Autowired
    private OmhStaticHotelInfoListAgent agent;

    @Test
    void getHotelInfo() {
        OmhStaticHotelInfoListRequest request = OmhStaticHotelInfoListRequest.builder()
            .language(Language.KO)
            .hotelCodes(List.of(907898L, 907625L))
            .lastUpdateDate(LocalDate.of(1970, 1, 1))
            .build();

        OmhStaticHotelInfoListResponse response = agent.getHotelInfo(request);
        assertThat(response.getHotels().size()).isEqualTo(2);
        log.info("{}", ObjectMapperUtils.writeAsString(response));
    }
}