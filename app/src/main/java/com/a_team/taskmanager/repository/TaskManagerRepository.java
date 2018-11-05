package com.a_team.taskmanager.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.net.Uri;

import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.util.List;

public class TaskManagerRepository {
    private static TaskManagerRepository mInstance;

    private MediatorLiveData<List<Task>> mObservableTasks;
    private final TaskManagerDatabase mDatabase;
    private Context mContext;

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
        mObservableTasks = new MediatorLiveData<>();

        mObservableTasks.addSource(mDatabase.taskDao().getAllTasks(), tasks -> {
            if (mDatabase.getIsDatabaseCreated().getValue() != null) {
                mObservableTasks.postValue(tasks);
            }
        });

        mContext = applicationContext;
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

    public void deleteTasks(Task... tasks) {
        removeNotifications(tasks);
        mDatabase.taskDao().deleteTasks(tasks);
    }

    private void removeNotifications(Task... tasks) {
        for (int i = 0; i < tasks.length; i++) {
            AlarmManager.removeNotification(mContext, tasks[i]);
        }
    }

    public File getPhotoFile(String fileName) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, fileName);
    }

    public void removePhotoFile(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
    }
}
