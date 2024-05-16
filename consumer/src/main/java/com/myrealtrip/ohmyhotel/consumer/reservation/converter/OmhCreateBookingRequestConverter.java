package com.myrealtrip.ohmyhotel.consumer.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.enumarate.BookingRequestCode;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.OmhRoomGuestDetail;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhBookingRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhCreateBookingContactPerson;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhCreateBookingRequest.OmhRoomGuestInfo;
import com.myrealtrip.unionstay.common.message.booking.BookingOrderMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class OmhCreateBookingRequestConverter {

    public OmhCreateBookingRequest toOmhCreateBookingRequest(Reservation reservation) {
        return OmhCreateBookingRequest.builder()
            .language(Language.KO)
            .channelBookingCode(reservation.getMrtReservationNo())
            .contactPerson(toOmhCreateBookingContactPerson(reservation))
            .hotelCode(reservation.getHotelId())
            .checkInDate(reservation.getCheckInDate())
            .checkOutDate(reservation.getCheckOutDate())
            .roomTypeCode(reservation.getRoomTypeCode())
            .roomToken(reservation.getAdditionalInfo().getRoomToken())
            .ratePlanCode(reservation.getRatePlanCode())
            .freeBreakfastName(null)
            .rooms(toOmhRoomGuestInfos(reservation))
            .requests(toRequests(reservation))
            .rateType(reservation.getAdditionalInfo().getRateType())
            .totalNetAmount(reservation.getDepositPrice())
            .build();
    }

    private OmhCreateBookingContactPerson toOmhCreateBookingContactPerson(Reservation reservation) {
        return OmhCreateBookingContactPerson.builder()
            .email(reservation.getCheckInUser().getEmail())
            .name(reservation.getCheckInUser().getFirstName() + " " + reservation.getCheckInUser().getLastName())
            .mobileNo(reservation.getCheckInUser().getContact())
            .build();
    }

    /**
     * 마리트에서는 대표 예약자 이름밖에 모르기 때문에 나머지 인원에 대해서는 TBA{number} 로 넣는다.
     */
    List<OmhRoomGuestInfo> toOmhRoomGuestInfos(Reservation reservation) {
        List<OmhRoomGuestDetail> omhRoomGuestDetails = new ArrayList<>();
        omhRoomGuestDetails.add(OmhRoomGuestDetail.builder()
                                    .firstName(reservation.getCheckInUser().getFirstName())
                                    .lastName(reservation.getCheckInUser().getLastName())
                                    .build());
        int cnt = 1;
        for (int i = 0; i < reservation.getGuestCount().getAdultCount() - 1; i++) {
            omhRoomGuestDetails.add(OmhRoomGuestDetail.builder()
                                        .firstName("TBA" + cnt)
                                        .lastName("TBA" + cnt)
                                        .build());
            cnt = cnt + 1;
        }
        for (int age : reservation.getGuestCount().getChildAges()) {
            omhRoomGuestDetails.add(OmhRoomGuestDetail.builder()
                                        .firstName("TBA" + cnt)
                                        .lastName("TBA" + cnt)
                                        .age(age)
                                        .build());
            cnt = cnt + 1;
        }

        OmhRoomGuestInfo omhRoomGuestInfo = OmhRoomGuestInfo.builder()
            .roomNo(1)
            .guests(omhRoomGuestDetails)
            .build();
        return List.of(omhRoomGuestInfo);
    }

    private List<OmhBookingRequest> toRequests(Reservation reservation) {
        if (StringUtils.isBlank(reservation.getSpecialRequest())) {
            return Collections.emptyList();
        }
        OmhBookingRequest omhBookingRequest = OmhBookingRequest.builder()
            .code(BookingRequestCode.BRQ99)
            .comment(reservation.getSpecialRequest())
            .build();
        return List.of(omhBookingRequest);
    }
}
