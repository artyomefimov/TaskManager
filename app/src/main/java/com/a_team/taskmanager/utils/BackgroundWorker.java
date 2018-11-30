package com.a_team.taskmanager.utils;

import android.content.Context;

import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.managers.alarms.AlarmDateTimeController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundWorker {
    private static BackgroundWorker instance;
    private Executor mExecutor;

    private BackgroundWorker() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public static BackgroundWorker getInstance() {
        if (instance == null)
            instance = new BackgroundWorker();
        return instance;
    }

    public void setNotificationsForRestoredTasks(Context context, List<Task> tasks) {
        mExecutor.execute(() -> {
            Date current = new Date();
            for (Task task : tasks) {
                if (isNotificationDateCorrect(task.getNotificationDate(), current))
                    AlarmManager.addNotification(context, task);
            }
        });
    }

    private boolean isNotificationDateCorrect(Long notificationDate, Date current) {
        return notificationDate != null && AlarmDateTimeController.isValidDateTime(current, new Date(notificationDate));
    }
}
