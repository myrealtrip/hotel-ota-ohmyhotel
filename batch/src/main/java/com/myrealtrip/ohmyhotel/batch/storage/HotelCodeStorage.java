package com.myrealtrip.ohmyhotel.batch.storage;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class HotelCodeStorage {

    private final Set<Long> hotelCodes = new HashSet<>();

    public void saveAll(List<Long> hotelCodes) {
        this.hotelCodes.addAll(hotelCodes);
    }
}
