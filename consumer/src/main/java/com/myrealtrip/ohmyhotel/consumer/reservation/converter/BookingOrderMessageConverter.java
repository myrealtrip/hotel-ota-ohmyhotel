package com.myrealtrip.ohmyhotel.consumer.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.OrderFormInfo;
import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.unionstay.common.constant.booking.OrderUserType;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import com.myrealtrip.unionstay.dto.hotelota.booking.request.CustomerDetail;
import com.myrealtrip.unionstay.dto.hotelota.booking.request.GuestDetail;
import org.springframework.stereotype.Component;

@Component
public class BookingOrderMessageConverter {

    public OrderFormInfo toOrderFormInfo(BookingOrderMessage message) {
        return OrderFormInfo.builder()
            .checkInUser(toCheckInUser(message.getRooms().get(0).getGuestDetail()))
            .reservationUser(toReservationUser(message.getCustomerDetail()))
            .specialRequest(message.getRooms().get(0).getSpecialRequest())
            .build();
    }

    private com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail toCheckInUser(GuestDetail guestDetail) {
        return com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail.builder()
            .firstName(guestDetail.getFirstName())
            .lastName(guestDetail.getLastName())
            .email(guestDetail.getEmail())
            .contact(guestDetail.getNumber())
            .build();
    }

    private com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail toReservationUser(CustomerDetail customerDe) {
        return com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestDetail.builder()
            .firstName(customerDe.getFirstName())
            .lastName(customerDe.getLastName())
            .email(customerDe.getEmail())
            .contact(customerDe.getNumber())
            .build();
    }


    public CanceledBy toCanceledBy(BookingOrderMessage message) {
        if (message.getOrderUserType() == OrderUserType.MANAGER) {
            return CanceledBy.MANAGER;
        }
        if (message.getOrderUserType() == OrderUserType.TRAVELER) {
            return CanceledBy.TRAVELER;
        }
        if (message.getOrderUserType() == OrderUserType.SYSTEM) {
            return CanceledBy.SYSTEM;
        }
        if (message.getOrderUserType() == OrderUserType.PARTNER) {
            return CanceledBy.PARTNER;
        }
        return null;
    }
}