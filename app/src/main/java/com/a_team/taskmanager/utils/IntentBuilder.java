package com.a_team.taskmanager.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.a_team.taskmanager.alarm.AlarmService;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.activity.TaskListActivity;

import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;
import static com.a_team.taskmanager.utils.UniqueCodeGenerator.getUniqueCode;

public class IntentBuilder {
    private static IntentBuilder instance;

    private IntentBuilder() {
    }

    public static IntentBuilder getInstance() {
        if (instance == null)
            instance = new IntentBuilder();
        return instance;
    }

    public PendingIntent buildPendingIntent(Context context, Task task) {
        Intent whatToStart = AlarmService.newIntent(context, task);
        return PendingIntent.getService(context, 0, whatToStart, PendingIntent.FLAG_ONE_SHOT);
    }

    public PendingIntent buildNotificationIntentForTaskListActivity(Context context) {
        Intent whatToStart = buildIntentForTaskListActivity(context);
        return PendingIntent.getActivity(context, getUniqueCode(), whatToStart, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent buildIntentForTaskListActivity(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    public PendingIntent buildNotificationIntentForSingleTaskActivity(Context context, Task task) {
        Intent whatToStart = buildIntentForSingleTaskActivity(context, task);
        return PendingIntent.getActivity(context, getUniqueCode(), whatToStart, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent buildIntentForSingleTaskActivity(Context context, Task task) {
        return new Intent(context, SingleTaskActivity.class)
                .putExtra(ARG_CURRENT_TASK, (Parcelable) task);
    }
}
