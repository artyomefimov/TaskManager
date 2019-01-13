package com.a_team.taskmanager.utils;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TypeConvertersTest {
    @Test
    public void longValueOfTest() {
        Date date = new Date();
        long dateValue = date.getTime();
        String dateString = String.valueOf(date.getTime());
        long parsedDateValue = Long.valueOf(dateString);
        assertEquals("date value: " + dateValue + ", parsed date value: " + parsedDateValue,
                dateValue, parsedDateValue);
    }

    @Test(expected = NumberFormatException.class)
    public void nullStringLongValueOf() {
        String dateString = null;
        Long.valueOf(dateString);
    }

    @Test(expected = NumberFormatException.class)
    public void nullStringLongParseLong() {
        String dateString = null;
        Long.parseLong(dateString);
    }

    @Test(expected = NumberFormatException.class)
    public void emptyStringLongValueOf() {
        String dateString = "";
        Long.valueOf(dateString);
    }

    @Test(expected = NumberFormatException.class)
    public void emptyStringLongParseLong() {
        String dateString = "";
        Long.parseLong(dateString);
    }
}
