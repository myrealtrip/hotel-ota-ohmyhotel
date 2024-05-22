package com.myrealtrip.ohmyhotel.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final DateTimeFormatter OMH_CANCEL_POLICY_DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter OMH_CANCEL_POLICY_DATE_TIME_FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
