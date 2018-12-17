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

public class PropertiesWriter {
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

                FileOutputStream fos = new FileOutputStream(propertiesFile);
                Properties properties = new Properties();

                properties.put(task.getId() + "", task.getNotificationDate().toString());

                properties.store(fos, null);

                fos.close();

                Log.i(TAG, "Notification for task: " + task.toString() + " was written to properties file.");
            } catch (IOException e) {
                Log.e(TAG, "Could not write notifications to file!", e);
            }
        });
    }

    private boolean isFileNotCorrect(File file) {
        return !file.exists() || !file.canWrite();
    }
}
