package com.a_team.taskmanager.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.activity.TaskListActivity;
import com.a_team.taskmanager.utils.NullStringProcessor;

public class NotificationBuilder {
    private static NotificationBuilder instance;
    private static final String CHANNEL_ID = "com.a_team.taskmanager.alarm.NotificationBuilder";
    private static final String CHANNEL_NAME = "Keep app notifications";

    private NotificationBuilder() {
    }

    public static NotificationBuilder getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationBuilder();
            createNotificationChannel(context);
        }
        return instance;
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    Notification buildNotification(Context context, Task task) {
        Resources resources = context.getResources();
        Intent onNotificationClickedIntent = buildIntent(context, task);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, onNotificationClickedIntent, 0);

        String contentTitle = NullStringProcessor.valueOf(task.getTitle());
        String contentText = NullStringProcessor.valueOf(task.getDescription());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long [] vibratorPattern = buildVibratorPattern();

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setTicker(resources.getString(R.string.notification_ticker))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_action_alarm)
                .setSound(alarmSound)
                .setVibrate(vibratorPattern)
                .setLights(Color.RED, 3000, 3000)
                .build();
    }

    private Intent buildIntent(Context context, Task task) {
        if (task == null) {
            return TaskListActivity.newIntent(context);
        } else {
            return SingleTaskActivity.newIntent(context, task);
        }
    }

    private long [] buildVibratorPattern() {
        long msToWaitBeforeTurningVibratorOn = 0;
        long msToKeepVibratorOn = 1000;
        long msToSleep = 1000;
        return new long[] {
                msToWaitBeforeTurningVibratorOn,
                msToKeepVibratorOn,
                msToSleep
        };
    }
}
