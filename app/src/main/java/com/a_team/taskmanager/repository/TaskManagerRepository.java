package com.a_team.taskmanager.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.utils.Optional;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class TaskManagerRepository {
    private static TaskManagerRepository mInstance;
    private static final String TAG = "TaskManagerRepository";

    private MediatorLiveData<List<Task>> mObservableTasks;
    private final TaskManagerDatabase mDatabase;
    private WeakReference<Context> mContext;
    private MediatorLiveData<Map<Long, Task>> mTasksWithNotifications;

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

        mTasksWithNotifications = new MediatorLiveData<>();

        mContext = new WeakReference<>(applicationContext);
    }

    public void subscribeOnTaskWithNotification(Task task) {
        Map<Long, Task> tasks = mTasksWithNotifications.getValue();
        if (tasks != null) {
            if (!tasks.containsKey(task.getId())) {
                tasks.put(task.getId(), task);
                mTasksWithNotifications.addSource(mDatabase.taskDao().getTask(task.getId()), t -> { // todo протестить
                    if (t != null)
                        mTasksWithNotifications.getValue().put(t.getId(), t);
                });
            }
        }
    }

    public Optional<Task> getTaskForNotification(long id) {
        if (mTasksWithNotifications.getValue() != null) {
            Task task = mTasksWithNotifications.getValue().get(id);
            return new Optional<>(task);
        } else
            return new Optional<>(null);
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

    public void insertTasks(Task... tasks) {
        mDatabase.taskDao().insertTasks(tasks);
    }

    private void removeNotifications(Task... tasks) {
        Context context = mContext.get();
        if (context != null) {
            for (Task task : tasks) {
                AlarmManager.removeNotification(context, task);
                removeTaskFromMediator(task);
            }
        }
    }

    private void removeTaskFromMediator(Task task) {
        if (mTasksWithNotifications.getValue() != null) {
            mTasksWithNotifications.getValue().remove(task.getId());
        }
        mTasksWithNotifications.removeSource(mDatabase.taskDao().getTask(task.getId()));
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
