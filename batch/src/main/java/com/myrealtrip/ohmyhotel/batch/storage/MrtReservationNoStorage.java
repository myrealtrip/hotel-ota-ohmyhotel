package com.myrealtrip.ohmyhotel.batch.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MrtReservationNoStorage {

    private final Set<String> mrtReservationNos = new HashSet<>();

    public void addAll(List<String> mrtReservationNos) {
        this.mrtReservationNos.addAll(mrtReservationNos);
    }

    public void clear() {
        this.mrtReservationNos.clear();
    }

    public List<String> getMrtReservationNos() {
        return new ArrayList<>(this.mrtReservationNos);
    }
}
