package com.a_team.taskmanager.notifications;

import com.a_team.taskmanager.ui.singletask.managers.alarms.AlarmDateTimeController;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlarmDateTimeControllerTest {
    @Test
    public void isValidDateTime() {
        Date current = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, 1);
        Date future = calendar.getTime();

        assertTrue(AlarmDateTimeController.isValidDateTime(current, future));

        calendar.setTime(current);
        calendar.add(Calendar.DATE, -1);
        Date past = calendar.getTime();

        assertFalse(AlarmDateTimeController.isValidDateTime(current, past));
        assertFalse(AlarmDateTimeController.isValidDateTime(current, current));
        assertFalse(AlarmDateTimeController.isValidDateTime(null, past));
        assertFalse(AlarmDateTimeController.isValidDateTime(current, null));
        assertFalse(AlarmDateTimeController.isValidDateTime(null, null));
    }
}