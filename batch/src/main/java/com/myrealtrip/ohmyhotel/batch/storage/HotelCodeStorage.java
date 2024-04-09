package com.myrealtrip.ohmyhotel.batch.storage;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HotelCodeStorage {

    private final Set<Long> hotelCodes = new HashSet<>();

    public void saveAll(List<Long> hotelCodes) {
        this.hotelCodes.addAll(hotelCodes);
    }

    public void clear() {
        this.hotelCodes.clear();
    }

    public List<Long> getHotelCodes() {
        return new ArrayList<>(this.hotelCodes);
    }
}
