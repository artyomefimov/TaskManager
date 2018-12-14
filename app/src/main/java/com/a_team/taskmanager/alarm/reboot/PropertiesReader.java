package com.a_team.taskmanager.alarm.reboot;

import android.content.Context;
import android.util.Log;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.alarm.AlarmConstants;
import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.entity.TaskBuilder;
import com.a_team.taskmanager.repository.TaskManagerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PropertiesReader {
    private static PropertiesReader instance;
    private static final String TAG = "PropertiesReader";

    private Executor mExecutor;

    private PropertiesReader() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public static PropertiesReader getInstance() {
        if (instance == null)
            instance = new PropertiesReader();
        return instance;
    }

    public void resetNotificationsFromProperties(Context context) {
        mExecutor.execute(() -> {
            try {
                File propertiesFile;
                if (context.getApplicationContext() instanceof BasicApp) {
                    Log.i(TAG, "Context is the instance of Keep App.");
                    propertiesFile = getFileFromRepository(context);
                } else {
                    Log.i(TAG, "Context is not the instance of Keep App.");
                    propertiesFile = getFile(context);
                }

                if (isFileNotCorrect(propertiesFile))
                    throw new RuntimeException("File is not exist or can not be read.");

                FileInputStream fis = new FileInputStream(propertiesFile);
                Properties properties = new Properties();
                properties.load(fis);
                fis.close();

                propertiesFile.delete();
                propertiesFile.createNewFile();

                resetNotifications(context, properties);
            } catch (IOException e) {
                Log.e(TAG, "Could not read properties from file.", e);
            }
        });
    }

    private boolean isFileNotCorrect(File file) {
        return !file.exists() || !file.canRead();
    }

    private File getFileFromRepository(Context context) {
        BasicApp app = ((BasicApp) context);
        TaskManagerRepository repository = app.getRepository();

        return repository.getNotificationPropertiesFile(context);
    }

    private File getFile(Context context) {
        File filesDir = context.getFilesDir();
        return new File(filesDir, AlarmConstants.PROPERTIES_FILE_NAME);
    }

    private void resetNotifications(Context context, Properties properties) {
        Enumeration<?> taskIds = properties.propertyNames();

        String key;
        Long id;
        Long notificationDate;

        while (taskIds.hasMoreElements()) {
            key = (String) taskIds.nextElement();
            id = Long.parseLong(key);
            notificationDate = (Long) properties.get(key);


            Task task = new TaskBuilder()
                    .setId(id)
                    .setNotificationDate(notificationDate)
                    .build();

            AlarmManager.addNotification(context, task);
        }
    }
}
