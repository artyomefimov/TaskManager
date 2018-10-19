package com.a_team.taskmanager.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";
    public static final String INTENT_NOTIFICATION = "com.a_team.taskmanager.notification.NotificationService";

    private static final int REQUEST_CODE = 0;
    private static final int FLAGS = 0;

    private static final long NOTIFICATION_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public static void setServiceAlarm(Context context, boolean isAlarmOn) {
        Intent intent = NotificationService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, REQUEST_CODE, intent, FLAGS);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isAlarmOn) {
            alarmManager
                    .setRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime(),
                            NOTIFICATION_INTERVAL_MS,
                            pendingIntent);

        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
