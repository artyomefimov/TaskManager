package com.a_team.taskmanager.ui.singletask.managers.alarms;

import java.util.Date;

public class AlarmDateTimeController {
    public static boolean isValidDateTime(Date currentTime, Date pickedTime) {
        if (currentTime == null || pickedTime == null)
            return false;
        return pickedTime.compareTo(currentTime) > 0;
    }
}