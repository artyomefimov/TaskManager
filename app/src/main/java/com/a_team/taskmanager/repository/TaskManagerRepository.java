package com.a_team.taskmanager.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class TaskManagerRepository {
    private static TaskManagerRepository mInstance;

    private final TaskManagerDatabase mDatabase;
    private WeakReference<Context> mContext;

    public static TaskManagerRepository getInstance(final TaskManagerDatabase database, final Context applicationContext) {
        if (mInstance == null) {
            synchronized (TaskManagerRepository.class) {
                if (mInstance == null) {
                    mInstance = new TaskManagerRepository(database, applicationContext);
                }
            }
        }
        return mInstance;
    }

    private TaskManagerRepository(final TaskManagerDatabase database, final Context applicationContext) {
        mDatabase = database;
        mContext = new WeakReference<>(applicationContext);
    }

    public Task getTaskForNotification(long id) {
        return mDatabase.taskDao().getTaskObject(id);
    }

    public LiveData<List<Task>> getTasks() {
        return mDatabase.taskDao().getAllTasks();
    }

    public void updateOrInsertTask(Task task) {
        if (isTaskWithCurrentIdExists(task.getId())) {
            mDatabase.taskDao().updateTasks(task);
        } else {
            mDatabase.taskDao().insert(task);
        }
    }

    private boolean isTaskWithCurrentIdExists(long taskId) {
        LiveData<Task> receivedTask = mDatabase.taskDao().getTask(taskId);
        return receivedTask.getValue() != null;
    }

    public void insertTasks(Task... tasks) {
        mDatabase.taskDao().insertTasks(tasks);
    }

    public void deleteTasks(Task... tasks) {
        removeNotifications(tasks);
        mDatabase.taskDao().deleteTasks(tasks);
    }

    private void removeNotifications(Task... tasks) {
        Context context = mContext.get();
        if (context != null) {
            for (Task task : tasks) {
                AlarmManager.removeNotification(context, task);
            }
        }
    }

    public File getFile(String fileName) {
        Context context = mContext.get();
        if (context != null) {
            File filesDir = context.getFilesDir();
            return new File(filesDir, fileName);
        } else
            return null;
    }

    public void removePhotoFile(Uri uri) {
        Context context = mContext.get();
        if (context != null) {
            context.getContentResolver().delete(uri, null, null);
        }
    }
}
