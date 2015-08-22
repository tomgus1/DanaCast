package com.sferadev.danacast.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class TimeUtils {
    public static int getCurrentDayInt() {
        DateTime dateTime = DateTime.now();
        return dateTime.dayOfWeek().get();
    }

    public static int getCurrentHourInt() {
        DateTime dateTime = DateTime.now();
        return dateTime.hourOfDay().get();
    }

    public static int compareDates(DateTime start, DateTime end) {
        return Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
    }
}
