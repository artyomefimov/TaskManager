package com.a_team.taskmanager.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.a_team.taskmanager.alarm.receivers.NotificationReceiver;
import com.a_team.taskmanager.entity.Task;

import static com.a_team.taskmanager.alarm.AlarmConstants.ACTION_SHOW_NOTIFICATION;
import static com.a_team.taskmanager.alarm.AlarmConstants.BUNDLE;
import static com.a_team.taskmanager.alarm.AlarmConstants.NOTIFICATION;
import static com.a_team.taskmanager.alarm.AlarmConstants.PERMISSION_PRIVATE;
import static com.a_team.taskmanager.alarm.AlarmConstants.REQUEST;
import static com.a_team.taskmanager.alarm.AlarmConstants.TASK_ID;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";
    private static final int REQUEST_CODE = 0;
    private static final int FLAGS = 0;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CURRENT_TASK, task);
        intent.putExtra(BUNDLE, bundle);
        intent.putExtra(TASK_ID, task.getFileUUID());
        return intent;
    }

    public AlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        Bundle bundle = intent.getBundleExtra(BUNDLE);
        Task task = bundle.getParcelable(ARG_CURRENT_TASK);
        NotificationBuilder builder = NotificationBuilder.getInstance(this);
        Notification notification = builder.buildNotification(this, task);
        //showBackgroundNotification(notification);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(REQUEST_CODE, notification);
    }

    private void showBackgroundNotification(Notification notification) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST, REQUEST_CODE);
        intent.putExtra(NOTIFICATION, notification);

        sendOrderedBroadcast(intent, PERMISSION_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }

    public static void setAlarm(Context context, Task task) {
        PendingIntent pendingIntent = buildIntent(context, task);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getNotificationDate(), pendingIntent);
        }
    }

    public static void removeAlarm(Context context, Task task) {
        PendingIntent pendingIntent = buildIntent(context, task);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static PendingIntent buildIntent(Context context, Task task) {
        Intent whatToStart = AlarmService.newIntent(context, task);
        return PendingIntent.getService(context, REQUEST_CODE, whatToStart, FLAGS);
    }
}
