package com.a_team.taskmanager.controller.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.util.List;
import java.util.logging.FileHandler;

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

        mObservableTasks.addSource(mDatabase.taskDao().getAllTasks(), new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                if (mDatabase.getIsDatabaseCreated().getValue() != null) {
                    mObservableTasks.postValue(tasks);
                }
            }
        });

        mContext = applicationContext;
    }

    public LiveData<Task> getTask(long taskId) {
        return mDatabase.taskDao().getTask(taskId);
    }

    public LiveData<List<Task>> getTasks() {
        return mDatabase.taskDao().getAllTasks();
    }

    public void updateOrInsertTask(Task task) {
        if (isTaskWithCurrentIdExists(task.getId())) {
            mDatabase.taskDao().updateTasks(task);
        } else {
            setTaskUuidIfNotSet(task);
            mDatabase.taskDao().insert(task);
        }
    }

    private void setTaskUuidIfNotSet(Task task) {
        if (task.getFileUUID() == null)
            task.setUUID();
    }

    private boolean isTaskWithCurrentIdExists(long taskId) {
        LiveData<Task> receivedTask = mDatabase.taskDao().getTask(taskId);
        return receivedTask.getValue() != null;
    }

    public void deleteTasks(Task... tasks) {
        mDatabase.taskDao().deleteTasks(tasks);
    }

    public File getPhotoFile(Task task) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, task.getPhotoFilename());
    }

    public void removePhotoFile(Uri uri) {
        mContext.getContentResolver().delete(uri, null, null);
    }
}
