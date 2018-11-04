package com.a_team.taskmanager.alarm.receivers;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import static com.a_team.taskmanager.alarm.AlarmConstants.NOTIFICATION;
import static com.a_team.taskmanager.alarm.AlarmConstants.REQUEST;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() != Activity.RESULT_OK || intent == null)
            return;

        int requestCode = intent.getIntExtra(REQUEST, 0);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);
    }
}
