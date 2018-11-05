package com.a_team.taskmanager.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.a_team.taskmanager.alarm.AlarmService;
import com.a_team.taskmanager.entity.Task;

public class IntentMaker {
    private static IntentMaker instance;

    private IntentMaker() {}

    public static IntentMaker getInstance() {
        if (instance == null)
            instance = new IntentMaker();
        return instance;
    }

    public PendingIntent buildPendingIntent(Context context, Task task) {
        Intent whatToStart = AlarmService.newIntent(context, task);
        return PendingIntent.getService(context, 0, whatToStart, PendingIntent.FLAG_ONE_SHOT);
    }
}
