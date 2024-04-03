package com.myrealtrip.ohmyhotel.core.domain.hotel.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.core.domain.hotel.converter.JpaFacilityListConverter;
import com.myrealtrip.ohmyhotel.core.domain.hotel.converter.JpaHotelDescriptionsConverter;
import com.myrealtrip.ohmyhotel.core.domain.hotel.converter.JpaPhotoListConverter;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Facility;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelDescriptions;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "hotel")
public class HotelEntity extends BaseEntity {

    @Id
    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "ko_name")
    private String koName;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private HotelStatus status;

    @Column(name = "region_code")
    private Long regionCode;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "hotel_type")
    private String hotelType;

    @Column(name = "star_rating")
    private String starRating;

    @Column(name = "floor_count")
    private String floorCount;

    @Column(name = "room_count")
    private String roomCount;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "fax_no")
    private String faxNo;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "ko_address")
    private String koAddress;

    @Column(name = "en_address")
    private String enAddress;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Column(name = "check_in_time")
    private String checkInTime;

    @Column(name = "check_out_time")
    private String checkOutTime;

    @Column(name = "established_date")
    private String establishedDate;

    @Column(name = "renovated_date")
    private String renovatedDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "recommendYn")
    private Boolean recommendYn;

    @Column(name = "legacy_hotel_code")
    private String legacyHotelCode;

    @Column(name = "last_update_date_time")
    private LocalDateTime lastUpdateDateTime;

    @Column(name = "ko_descriptions")
    @Convert(converter = JpaHotelDescriptionsConverter.class)
    private HotelDescriptions koDescriptions;

    @Column(name = "en_descriptions")
    @Convert(converter = JpaHotelDescriptionsConverter.class)
    private HotelDescriptions enDescriptions;

    @Column(name = "facilities")
    @Convert(converter = JpaFacilityListConverter.class)
    private List<Facility> facilities;

    @Column(name = "photos")
    @Convert(converter = JpaPhotoListConverter.class)
    private List<Photo> photos;

    public void updateStatus(HotelStatus status) {
        this.status = status;
    }
}
