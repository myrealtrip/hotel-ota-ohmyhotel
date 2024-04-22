package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.CoreTestContext;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CoreTestContext.class, properties = "spring.profiles.active=dev")
class OrderProviderTest {

    @Autowired
    private OrderProvider orderProvider;

    @Test
    @Rollback(value = false)
    @Disabled
    void upsert() {
        Order order = Order.builder()
            .hotelId(1L)
            .hotelName("hotelName")
            .roomTypeCode("roomTypeCode")
            .roomTypeName("roomTypeName")
            .ratePlanCode("ratePlanCode")
            .ratePlanName("ratePlanName")
            .checkInDate(LocalDate.now())
            .checkOutDate(LocalDate.now())
            .salePrice(BigDecimal.valueOf(100))
            .zeroMarginApply(true)
            .zeroMarginApplyPrice(BigDecimal.valueOf(50))
            .mrtCommissionRate(BigDecimal.valueOf(10))
            .guestCount(GuestCount.builder()
                            .adultCount(2)
                            .childCount(0)
                            .childAges(List.of())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder().build())
            .build();

        orderProvider.upsert(order);
    }
}