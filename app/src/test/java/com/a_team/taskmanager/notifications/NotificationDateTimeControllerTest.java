package com.a_team.taskmanager.notifications;

import com.a_team.taskmanager.ui.singletask.managers.notifications.NotificationDateTimeController;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class NotificationDateTimeControllerTest {
    @Test
    public void isValidDateTime() {
        Date current = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, 1);
        Date future = calendar.getTime();

        assertTrue(NotificationDateTimeController.isValidDateTime(current, future));

        calendar.setTime(current);
        calendar.add(Calendar.DATE, -1);
        Date past = calendar.getTime();

        assertFalse(NotificationDateTimeController.isValidDateTime(current, past));
        assertFalse(NotificationDateTimeController.isValidDateTime(current, current));
        assertFalse(NotificationDateTimeController.isValidDateTime(null, past));
        assertFalse(NotificationDateTimeController.isValidDateTime(current, null));
        assertFalse(NotificationDateTimeController.isValidDateTime(null, null));
    }
}