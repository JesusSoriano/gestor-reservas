package com.booking.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jesús Soriano
 */
public class DateService implements Serializable {

    public static Date getDawnDay(Date date) {
        // getting the time of the beginning of the day
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        return c.getTime();
    }

    public static Date getMidnightDay(Date date) {
        // getting the time of the end of the day
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);

        c1.set(Calendar.HOUR_OF_DAY, 23);
        c1.set(Calendar.MINUTE, 59);
        c1.set(Calendar.SECOND, 59);
        return c1.getTime();
    }
}