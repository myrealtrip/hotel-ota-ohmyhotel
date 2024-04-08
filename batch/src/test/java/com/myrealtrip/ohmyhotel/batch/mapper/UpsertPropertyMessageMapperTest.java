package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class UpsertPropertyMessageMapperTest {

    private UpsertPropertyMessageMapper mapper = Mappers.getMapper(UpsertPropertyMessageMapper.class);

    @Test
    void toCheckInStartTime() {
        String time1 = "2:00 PM ~ midnight";
        String time2 = "6:00 PM";
        String time3 = "2:00 AM";
        String time4 = "14:00";
        String time5 = "12:00 PM";
        String time6 = "1500";

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time1).build()))
            .isEqualTo(LocalTime.of(14, 0));

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time2).build()))
            .isEqualTo(LocalTime.of(18, 0));

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time3).build()))
            .isEqualTo(LocalTime.of(2, 0));

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time4).build()))
            .isEqualTo(LocalTime.of(14, 0));

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time5).build()))
            .isEqualTo(LocalTime.of(12, 0));

        assertThat(mapper.toCheckInStartTime(Hotel.builder().checkInTime(time6).build()))
            .isNull();
    }

    @Test
    void toCheckInEndTime() {
        String time1 = "2:00 PM ~ midnight";
        String time2 = "2:00 PM ~ 11:00 PM";
        String time3 = "3:00 PM ~ 1:00 AM";
        String time4 = "14:00";
        String time5 = "12:00 PM";
        String time6 = "1500";

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time1).build()))
            .isEqualTo(LocalTime.of(0, 0));

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time2).build()))
            .isEqualTo(LocalTime.of(23, 0));

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time3).build()))
            .isEqualTo(LocalTime.of(1, 0));

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time4).build()))
            .isNull();

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time5).build()))
            .isNull();

        assertThat(mapper.toCheckInEndTime(Hotel.builder().checkInTime(time6).build()))
            .isNull();
    }

    @Test
    void toCheckOutStartTime() {
        String time1 = "2:00 PM ~ midnight";
        String time2 = "14:00";

        assertThat(mapper.toCheckOutStartTime(Hotel.builder().checkOutTime(time1).build()))
            .isEqualTo(LocalTime.of(14, 0));

        assertThat(mapper.toCheckOutStartTime(Hotel.builder().checkOutTime(time2).build()))
            .isNull();
    }

    @Test
    void toCheckOutEndTime() {
        String time1 = "2:00 PM ~ midnight";
        String time2 = "14:00";

        assertThat(mapper.toCheckOutEndTime(Hotel.builder().checkOutTime(time1).build()))
            .isEqualTo(LocalTime.of(0, 0));

        assertThat(mapper.toCheckOutEndTime(Hotel.builder().checkOutTime(time2).build()))
            .isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    void a() {
        Hotel hotel = Hotel.builder()
            .checkInTime("2:00 PM")
            .checkOutTime("11:00 AM")
            .build();

        mapper.toGuestPolicy(hotel);
    }
}