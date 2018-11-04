package com.a_team.taskmanager.alarm;

import android.content.Context;

import com.a_team.taskmanager.entity.Task;

public class AlarmManager {
    public static void addNotification(Context context, Task task) {
        AlarmService.setAlarm(context, task);
    }

    public static void removeNotification(Context context, Task task) {
        AlarmService.removeAlarm(context, task);
    }
}
