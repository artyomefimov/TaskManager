package com.a_team.taskmanager.notifications;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotificationDateTimeTest {

    @Test
    public void testCalendar() {
        Calendar calendar = Calendar.getInstance();
        assertEquals(2018, calendar.get(Calendar.YEAR));
        assertEquals(9, calendar.get(Calendar.MONTH));
        assertEquals(calendar.getTime(), new Date(System.currentTimeMillis()));
    }

    @Test
    public void testDateComparator() {
        Date current = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, 1);
        Date future = calendar.getTime();

        assertTrue(future.compareTo(current) > 0);

        calendar.setTime(current);
        calendar.add(Calendar.DATE, -1);
        Date past = calendar.getTime();

        assertTrue(past.compareTo(current) < 0);
    }
}
