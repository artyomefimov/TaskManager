package com.a_team.taskmanager.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.model.Task;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskManagerRepository {
    private static TaskManagerRepository mInstance;

    private MediatorLiveData<List<Task>> mObservableTasks;
    private final TaskManagerDatabase mDatabase;

    public static TaskManagerRepository getInstance(final TaskManagerDatabase database) {
        if (mInstance == null) {
            synchronized (TaskManagerRepository.class) {
                if (mInstance == null) {
                    mInstance = new TaskManagerRepository(database);
                }
            }
        }
        return mInstance;
    }

    private TaskManagerRepository(final TaskManagerDatabase database) {
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
            mDatabase.taskDao().insert(task);
        }
    }

    private boolean isTaskWithCurrentIdExists(long taskId) {
        LiveData<Task> receivedTask = mDatabase.taskDao().getTask(taskId);
        return receivedTask.getValue() != null;
    }

    public void deleteTasks(Task... tasks) {
        mDatabase.taskDao().deleteTasks(tasks);
    }
}
