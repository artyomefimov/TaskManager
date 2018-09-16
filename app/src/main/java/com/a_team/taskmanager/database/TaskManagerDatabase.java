package com.a_team.taskmanager.database;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.a_team.taskmanager.database.dao.TaskDao;
import com.a_team.taskmanager.model.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskManagerDatabase extends RoomDatabase {
    private static final String DB_NAME = "task_manager_db";

    private static TaskManagerDatabase instance;
    private MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static TaskManagerDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (TaskManagerDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            TaskManagerDatabase.class,
                            DB_NAME)
                            .build();
                    instance.updateIsDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private void updateIsDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DB_NAME).exists()) {
            setIsDatabaseCreated();
        }
    }

    private void setIsDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public MutableLiveData<Boolean> getIsDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    public abstract TaskDao taskDao();
}
