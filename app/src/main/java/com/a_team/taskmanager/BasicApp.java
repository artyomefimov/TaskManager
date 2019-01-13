package com.a_team.taskmanager;

import android.app.Application;

import com.a_team.taskmanager.database.TaskManagerDatabase;
import com.a_team.taskmanager.repository.TaskManagerRepository;

public class BasicApp extends Application {
    public TaskManagerDatabase getDatabase() {
        return TaskManagerDatabase.getDatabase(this);
    }

    public TaskManagerRepository getRepository() {
        return TaskManagerRepository.getInstance(getDatabase(), getApplicationContext());
    }
}