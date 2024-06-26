package com.myrealtrip.ohmyhotel.batch.storage;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HotelCodeStorage {

    private final Set<Long> hotelCodes = new HashSet<>();

    public void addAll(List<Long> hotelCodes) {
        this.hotelCodes.addAll(hotelCodes);
    }

    public void clear() {
        this.hotelCodes.clear();
    }

    public List<Long> getHotelCodes() {
        return new ArrayList<>(this.hotelCodes);
    }

    public void removeAll(List<Long> hotelCodes) {
        this.hotelCodes.removeAll(hotelCodes);
    }
}
