package com.myrealtrip.ohmyhotel.core.domain.hotel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HotelDescriptions {

    private String introduction;

    private String getThere;

    private String hotelFacility;

    private String roomFacility;

    private String attractions;

    private String cautions;

    private String specialDescription;
}
