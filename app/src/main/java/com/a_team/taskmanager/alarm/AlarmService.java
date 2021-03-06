package com.a_team.taskmanager.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.alarm.reboot.PropertiesDeleteHelper;
import com.a_team.taskmanager.alarm.reboot.PropertiesWriter;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.a_team.taskmanager.utils.IntentBuilder;

import java.io.File;

import static com.a_team.taskmanager.alarm.AlarmConstants.ACTION_SHOW_NOTIFICATION;
import static com.a_team.taskmanager.alarm.AlarmConstants.BUNDLE;
import static com.a_team.taskmanager.alarm.AlarmConstants.NOTIFICATION;
import static com.a_team.taskmanager.alarm.AlarmConstants.NOTIFICATION_ID;
import static com.a_team.taskmanager.alarm.AlarmConstants.PERMISSION_PRIVATE;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() != Activity.RESULT_OK || intent == null)
                return;

            int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
            Notification notification = intent.getParcelableExtra(NOTIFICATION);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, notification);
        }
    };

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CURRENT_TASK, task);
        intent.putExtra(BUNDLE, bundle);
        intent.setData(Uri.parse("alarm:" + task.getPhotoFilename()));
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
        Task fromBundle = bundle.getParcelable(ARG_CURRENT_TASK);

        Task fromRepository = getActualTaskForNotification(fromBundle.getId());

        if (fromRepository != null) {
            int notificationId = fromRepository.getPhotoFilename().hashCode();

            NotificationBuilder builder = NotificationBuilder.getInstance(this);
            Notification notification = builder
                    .buildNotification(this, fromRepository);

            showBackgroundNotification(notification, notificationId);
        }
    }

    private Task getActualTaskForNotification(long id) {
        TaskManagerRepository repository = ((BasicApp) getApplication()).getRepository();
        return repository.getTaskForNotification(id);
    }

    private void showBackgroundNotification(Notification notification, int notificationId) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION)
                .putExtra(NOTIFICATION, notification)
                .putExtra(NOTIFICATION_ID, notificationId);

        sendOrderedBroadcast(intent, PERMISSION_PRIVATE, mOnShowNotification, null,
                Activity.RESULT_OK, null, null);
    }

    public static void setAlarm(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            PendingIntent pendingIntent = IntentBuilder.buildPendingIntent(context, task);
            scheduleAlarm(alarmManager, task, pendingIntent);
        }
    }

    private static void scheduleAlarm(AlarmManager alarmManager, Task task, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getNotificationDate(), pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getNotificationDate(), pendingIntent);
    }

    public static void removeAlarm(Context context, Task task) {
        PendingIntent pendingIntent = IntentBuilder.buildPendingIntent(context, task);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        task.setNotificationDate(null);
    }
}
