package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.CoreTestContext;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CoreTestContext.class, properties = "spring.profiles.active=dev")
class ReservationProviderTest {

    @Autowired
    private ReservationProvider reservationProvider;

    @Test
    @Rollback(value = false)
    @Disabled
    void upsert() {
        Reservation reservation = Reservation.builder()
            .reservationId(1L)
            .orderId(1L)
            .mrtReservationNo("mrtReservationNo")
            .omhBookCode("omhBookCode")
            .hotelConfirmNo("hotelConfirmNo")
            .reservationStatus(ReservationStatus.CANCEL_SUCCESS)
            .hotelId(1L)
            .hotelName("hotelName")
            .roomTypeCode("roomTypeCode")
            .roomTypeName("roomTypeName")
            .ratePlanCode("ratePlanCode")
            .ratePlanName("ratePlanName")
            .checkInDate(LocalDate.now())
            .checkOutDate(LocalDate.now())
            .salePrice(BigDecimal.valueOf(110))
            .depositPrice(BigDecimal.valueOf(100))
            .zeroMarginApply(false)
            .zeroMarginApplyPrice(null)
            .mrtCommissionRate(BigDecimal.valueOf(10))
            .guestCount(GuestCount.builder()
                            .adultCount(2)
                            .childCount(0)
                            .childAges(List.of())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder().build())
            .reservationUser(GuestDetail.builder()
                                 .firstName("firstName")
                                 .lastName("lastName")
                                 .email("email")
                                 .contact("contact")
                                 .build())
            .checkInUser(GuestDetail.builder()
                             .firstName("firstName")
                             .lastName("lastName")
                             .email("email")
                             .contact("contact")
                             .build())
            .specialRequest("specialRequest")
            .cancelPenaltyAmount(BigDecimal.valueOf(100))
            .bookingErrorCode("bookingErrorCode")
            .logs("logs")
            .confirmedAt(LocalDateTime.now())
            .canceledAt(LocalDateTime.now())
            .canceledBy(CanceledBy.TRAVELER)
            .cancelReason("cancelReason")
            .cancelReasonType("cancelReasonType")
            .build();

        reservationProvider.upsert(reservation);
    }
}