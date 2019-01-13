package com.a_team.taskmanager.alarm.reboot;

import android.util.Log;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.utils.WorkerThreadFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PropertiesWriter extends PropertiesOperation {
    private static PropertiesWriter instance;
    private static final String TAG = "PropertiesWriter";

    private Executor mExecutor;

    private PropertiesWriter() {
        mExecutor = Executors.newSingleThreadExecutor(new WorkerThreadFactory());
    }

    public static PropertiesWriter getInstance() {
        if (instance == null)
            instance = new PropertiesWriter();
        return instance;
    }

    public void writeNotificationToPropertiesFile(File propertiesFile, Task task) {
        mExecutor.execute(() -> {
            try {
                if (isFileNotCorrect(propertiesFile)) {
                    boolean isCreated = propertiesFile.createNewFile();
                    Log.i(TAG, "New File created = " + isCreated);
                }
                Log.i(TAG, "File correct. exists = " + propertiesFile.exists() +
                        ", can write = " + propertiesFile.canWrite());

                Properties properties = loadPropertiesFromFile(propertiesFile);

                writePropertyIfNotPresent(properties, task, propertiesFile);

                Log.i(TAG, "Notification for task: " + task.toString() + " was written to properties file.");
            } catch (IOException e) {
                Log.e(TAG, "Could not write notifications to file!", e);
            }
        });
    }

    private void writePropertyIfNotPresent(Properties properties, Task task, File propertiesFile) throws IOException {
        properties.put(task.getId() + "", task.getNotificationDate().toString());
        writePropertiesToFile(properties, propertiesFile);
    }
}
