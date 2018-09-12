package com.a_team.taskmanager.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.a_team.taskmanager.database.dao.TaskDao;
import com.a_team.taskmanager.model.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskManagerDatabase extends RoomDatabase {
    private static TaskManagerDatabase instance;

    public static TaskManagerDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (TaskManagerDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            TaskManagerDatabase.class,
                            "task_manager_db")
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract TaskDao taskDao();
}
