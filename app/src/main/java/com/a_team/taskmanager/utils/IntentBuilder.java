package com.a_team.taskmanager.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;

import com.a_team.taskmanager.alarm.AlarmService;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.activity.TaskListActivity;

import java.io.File;

import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.FILE_PROVIDER;
import static com.a_team.taskmanager.utils.UniqueRequestCodeGenerator.getUniqueRequestCode;

public class IntentBuilder {
    private static final String CHOOSING_FILE = "Choose a backup file";

    public static PendingIntent buildPendingIntent(Context context, Task task) {
        Intent whatToStart = AlarmService.newIntent(context, task);
        return PendingIntent.getService(context, 0, whatToStart, PendingIntent.FLAG_ONE_SHOT); // todo проверить необходимость уникального request code'a, флаг pending intent'a
    }

    public static PendingIntent buildNotificationIntentForTaskListActivity(Context context) {
        Intent whatToStart = buildIntentForTaskListActivity(context);
        return PendingIntent.getActivity(context, getUniqueRequestCode(), whatToStart, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Intent buildIntentForTaskListActivity(Context context) {
        return new Intent(context, TaskListActivity.class);
    }

    public static PendingIntent buildNotificationIntentForSingleTaskActivity(Context context, Task task) {
        Intent whatToStart = buildIntentForSingleTaskActivity(context, task);
        return PendingIntent.getActivity(context, getUniqueRequestCode(), whatToStart, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Intent buildIntentForSingleTaskActivity(Context context, Task task) {
        return new Intent(context, SingleTaskActivity.class)
                .putExtra(ARG_CURRENT_TASK, (Parcelable) task);
    }

    public static Intent buildIntentForSharingBackupFile(Activity activity, File backup) {
        Uri uri = FileProvider.getUriForFile(activity, FILE_PROVIDER, backup);
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType("application/zip")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static Intent buildIntentForChoosingBackupFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("application/zip")
                .addCategory(Intent.CATEGORY_OPENABLE);
        return Intent.createChooser(intent, CHOOSING_FILE);
    }
}
