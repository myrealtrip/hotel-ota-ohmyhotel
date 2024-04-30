package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class OrderFormInfo {

    private GuestDetail reservationUser;

    private GuestDetail checkInUser;

    private String specialRequest;
}
