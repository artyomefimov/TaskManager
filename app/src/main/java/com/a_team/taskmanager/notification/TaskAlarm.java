package com.a_team.taskmanager.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class TaskAlarm implements Runnable {
    private Calendar mDateTime;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    public TaskAlarm(Context context, Calendar dateTime) {
        mDateTime = dateTime;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        createPendingIntent(context);
    }

    private void createPendingIntent(Context context) {
        Intent intent = NotificationService.newIntent(context);
        intent.putExtra(NotificationService.INTENT_NOTIFICATION, true);
        mPendingIntent = PendingIntent.getService(context, 0, intent, 0);
    }

    @Override
    public void run() {
        scheduleNotification();
    }

    private void scheduleNotification() {
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mDateTime.getTimeInMillis(), mPendingIntent);
    }
}
