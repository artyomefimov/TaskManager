package com.a_team.taskmanager.utils;

import java.util.Date;

public class AlarmDateTimeController {
    public static boolean isValidDateTime(Date currentTime, Date pickedTime) {
        if (currentTime == null || pickedTime == null)
            return false;
        return pickedTime.compareTo(currentTime) > 0;
    }
}