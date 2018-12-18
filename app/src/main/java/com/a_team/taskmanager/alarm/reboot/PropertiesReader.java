package com.a_team.taskmanager.alarm.reboot;

import android.content.Context;
import android.util.Log;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.alarm.AlarmConstants;
import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.entity.TaskBuilder;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.a_team.taskmanager.utils.AlarmDateTimeController;
import com.a_team.taskmanager.utils.WorkerThreadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PropertiesReader extends PropertiesOperation {
    private static PropertiesReader instance;
    private static final String TAG = "PropertiesReader";

    private Executor mExecutor;

    private PropertiesReader() {
        mExecutor = Executors.newSingleThreadExecutor(new WorkerThreadFactory());
    }

    public static PropertiesReader getInstance() {
        if (instance == null)
            instance = new PropertiesReader();
        return instance;
    }

    public void resetNotificationsFromProperties(Context context) {
        mExecutor.execute(() -> {
            try {
                File propertiesFile = getPropertiesFile(context);
                if (isFileNotCorrect(propertiesFile))
                    throw new RuntimeException("File is not exist or can not be read.");

                Properties properties = loadPropertiesFromFile(propertiesFile);

                recreatePropertiesFile(propertiesFile); // todo проверить необходимость

                resetNotifications(context, properties);
            } catch (IOException e) {
                Log.e(TAG, "Could not read properties from file.", e);
            }
        });
    }

    private File getPropertiesFile(Context context) {
        if (context.getApplicationContext() instanceof BasicApp) {
            Log.i(TAG, "Context is the instance of Keep App.");
            return getFileFromRepository(context);
        } else { // todo протестить
            Log.i(TAG, "Context is not the instance of Keep App.");
            return getFile(context);
        }
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

    private void recreatePropertiesFile(File propertiesFile) throws IOException {
        propertiesFile.delete();
        propertiesFile.createNewFile();
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

            if (AlarmDateTimeController.isValidDateTime(new Date(), new Date(notificationDate))) {
                Task task = new TaskBuilder()
                        .setId(id)
                        .setNotificationDate(notificationDate)
                        .build();

                AlarmManager.addNotification(context, task);
            }
        }
    }
}
