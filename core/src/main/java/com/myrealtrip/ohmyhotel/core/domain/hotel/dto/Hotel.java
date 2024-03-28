package com.myrealtrip.ohmyhotel.core.domain.hotel.dto;

import com.myrealtrip.ohmyhotel.core.domain.ModifyInfo;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Hotel extends ModifyInfo {

    private Long hotelId;

    private String koName;

    private String enName;

    private HotelStatus status;

    private String regionCode;

    private String regionName;

    private String countryCode;

    private String countryName;

    private String hotelType;

    private String starRating;

    private String floorCount;

    private String roomCount;

    private String phoneNo;

    private String faxNo;

    private String zipCode;

    private String koAddress;

    private String enAddress;

    private String homepageUrl;

    private String checkInTime;

    private String checkOutTime;

    private LocalDate establishedDate;

    private LocalDate renovatedDate;

    private Double latitude;

    private Double longitude;

    private Boolean recommendYn;

    private String legacyHotelCode;

    private LocalDateTime lastUpdateDateTime;

    private HotelDescriptions koDescriptions;

    private HotelDescriptions enDescriptions;

    private List<Facility> facilities;

    private List<Photo> photos;
}
