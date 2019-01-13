package com.a_team.taskmanager.alarm.reboot;

import android.util.Log;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.utils.WorkerThreadFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PropertiesDeleteHelper extends PropertiesOperation {
    private static PropertiesDeleteHelper instance;
    private static final String TAG = "PropertiesDeleteHelper";

    private Executor mExecutor;

    private PropertiesDeleteHelper() {
        mExecutor = Executors.newSingleThreadExecutor(new WorkerThreadFactory());
    }

    public static PropertiesDeleteHelper getInstance() {
        if (instance == null)
            instance = new PropertiesDeleteHelper();
        return instance;
    }

    public void deleteNotificationFromProperties(File propertiesFile, Task task) {
        mExecutor.execute(() -> {
            try {
                if (isFileNotCorrect(propertiesFile)) {
                    throw new RuntimeException("File is not exist or can not get access to its content.");
                }
                Log.i(TAG, "File correct. exists = " + propertiesFile.exists() +
                        ", can write = " + propertiesFile.canWrite() +
                        ", can read = " + propertiesFile.canRead());

                Properties properties = loadPropertiesFromFile(propertiesFile);

                removePropertyIfPresent(properties, task);

                writePropertiesToFile(properties, propertiesFile);

                Log.i(TAG, "Unnecessary notification for task: " + task.toString() + " was removed from properties file.");
            } catch (IOException e) {
                Log.e(TAG, "Could not remove notifications from file!", e);
            }
        });
    }

    private Properties removePropertyIfPresent(Properties properties, Task task) {
        String property = properties.getProperty(task.getId() + "");
        if (property != null) {
            Log.i(TAG, "Property " + task.getId() + " was found. Removing it.");

            properties.remove(task.getId() + "");
            return properties;
        } else
            return properties;
    }
}
