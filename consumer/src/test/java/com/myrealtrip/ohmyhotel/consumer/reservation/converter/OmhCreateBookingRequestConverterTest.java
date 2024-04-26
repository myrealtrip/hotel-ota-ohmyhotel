package com.myrealtrip.ohmyhotel.consumer.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.OmhRoomGuestDetail;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhRoomGuestInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OmhCreateBookingRequestConverterTest {

    private OmhCreateBookingRequestConverter converter = new OmhCreateBookingRequestConverter();

    @Test
    @DisplayName("투숙 인원 컨버팅 테스트")
    void toOmhRoomGuestInfos() {
        // given
        Reservation reservation = Reservation.builder()
            .checkInUser(GuestDetail.builder()
                             .firstName("minsu")
                             .lastName("kang")
                             .build())
            .guestCount(GuestCount.builder()
                            .adultCount(2)
                            .childCount(2)
                            .childAges(List.of(10, 11))
                            .build())
            .build();

        // when
        List<OmhRoomGuestInfo> omhRoomGuestInfos = converter.toOmhRoomGuestInfos(reservation);

        // then
        OmhRoomGuestInfo expectedOmhRoomGuestInfo = OmhRoomGuestInfo.builder()
            .roomNo(1)
            .guests(List.of(
                OmhRoomGuestDetail.builder()
                    .firstName("minsu")
                    .lastName("kang")
                    .build(),
                OmhRoomGuestDetail.builder()
                    .firstName("TBA1")
                    .lastName("TBA1")
                    .build(),
                OmhRoomGuestDetail.builder()
                    .firstName("TBA2")
                    .lastName("TBA2")
                    .age(10)
                    .build(),
                OmhRoomGuestDetail.builder()
                    .firstName("TBA3")
                    .lastName("TBA3")
                    .age(11)
                    .build()
                ))
            .build();
        assertThat(omhRoomGuestInfos).isEqualTo(List.of(expectedOmhRoomGuestInfo));
    }
}