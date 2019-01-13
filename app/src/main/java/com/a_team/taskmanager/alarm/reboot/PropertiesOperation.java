package com.a_team.taskmanager.alarm.reboot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class PropertiesOperation {

    protected boolean isFileNotCorrect(File file) {
        return !file.exists() || !file.canWrite() || !file.canRead();
    }

    protected Properties loadPropertiesFromFile(File propertiesFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties;
        }
    }

    protected Properties writePropertiesToFile(Properties properties, File propertiesFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
            properties.store(fos, null);
            return properties;
        }
    }
}
