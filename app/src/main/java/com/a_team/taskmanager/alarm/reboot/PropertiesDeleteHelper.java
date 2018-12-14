package com.a_team.taskmanager.alarm.reboot;

import android.content.Context;
import android.util.Log;

import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PropertiesDeleteHelper {
    private static PropertiesDeleteHelper instance;
    private static final String TAG = "PropertiesDeleteHelper";

    private Executor mExecutor;

    private PropertiesDeleteHelper() {
        mExecutor = Executors.newSingleThreadExecutor();
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

                Properties properties = new Properties();

                getProperties(propertiesFile, properties);

                removePropertyIfPresent(propertiesFile, properties, task);


                Log.i(TAG, "Unnecessary notification for task: " + task.toString() + " was removed from properties file.");
            } catch (IOException e) {
                Log.e(TAG, "Could not remove notifications from file!", e);
            }
        });
    }

    private boolean isFileNotCorrect(File file) {
        return !file.exists() || !file.canWrite() || !file.canRead();
    }

    private void getProperties(File propertiesFile, Properties properties) throws IOException {
        FileInputStream fis = new FileInputStream(propertiesFile);

        properties.load(fis);

        fis.close();
    }

    private void removePropertyIfPresent(File propertiesFile, Properties properties, Task task) throws IOException {
        String property = properties.getProperty(task.getId() + "");
        if (property != null) {
            Log.i(TAG, "Property " + task.getId() + " was found. Removing it and overwriting a file.");

            properties.remove(task.getId() + "");
            propertiesFile.delete();
            propertiesFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(propertiesFile);

            properties.store(fos, null);

            fos.close();
        }
    }
}
