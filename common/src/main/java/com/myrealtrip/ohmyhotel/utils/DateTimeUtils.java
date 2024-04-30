package com.myrealtrip.ohmyhotel.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class DateTimeUtils {

    public static LocalDateTime now(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    public static boolean goe(LocalDateTime target, LocalDateTime compare) {
        return target.equals(compare) || target.isAfter(compare);
    }

    public static boolean loe(LocalDateTime target, LocalDateTime compare) {
        return target.equals(compare) || target.isBefore(compare);
    }
}
