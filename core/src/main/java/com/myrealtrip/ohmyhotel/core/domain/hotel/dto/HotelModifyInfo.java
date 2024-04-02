package com.myrealtrip.ohmyhotel.core.domain.hotel.dto;

import com.myrealtrip.ohmyhotel.core.domain.ModifyInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class HotelModifyInfo extends ModifyInfo {

    private Long hotelId;

    public HotelModifyInfo(Long hotelId,
                           LocalDateTime createdAt,
                           String createdBy,
                           LocalDateTime updatedAt,
                           String updatedBy,
                           LocalDateTime deletedAt,
                           String deletedBy) {
        super(createdAt, createdBy, updatedAt, updatedBy, deletedAt, deletedBy);
        this.hotelId = hotelId;
    }
}
