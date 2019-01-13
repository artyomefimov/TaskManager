package com.a_team.taskmanager.backup;

import android.content.Context;
import android.net.Uri;

import com.a_team.taskmanager.backup.utils.JsonFileContentToStringParser;
import com.a_team.taskmanager.backup.utils.UnpackUtil;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.a_team.taskmanager.backup.utils.BackupConstants.FILE_NOT_FOUND_READ;
import static com.a_team.taskmanager.backup.utils.BackupConstants.IO_EXCEPTION_READ;
import static com.a_team.taskmanager.backup.utils.BackupConstants.TEMP_BACKUP_FILE;
import static com.a_team.taskmanager.backup.utils.FileRecognizer.isTaskFile;

public class FileDataReader {
    private List<Task> mTasksFromBackup;

    public List<Task> readTasksFromBackup(Context context, TaskManagerRepository repository, Uri backupUri) throws IOException {
        try {
            checkIfContextNotNull(context);
            readTasksAndAddPhotosToLocalRepository(context, repository, backupUri);
            return mTasksFromBackup;
        } catch (FileNotFoundException e) {
            throw new IOException(FILE_NOT_FOUND_READ, e);
        } catch (IOException e) {
            throw new IOException(IO_EXCEPTION_READ, e);
        }
    }

    private void checkIfContextNotNull(Context context) throws IOException {
        if (context == null)
            throw new IOException();
    }

    private void readTasksAndAddPhotosToLocalRepository(Context context, TaskManagerRepository repository, Uri backupUri) throws IOException {
        mTasksFromBackup = new ArrayList<>();

        File temp = repository.getFile(TEMP_BACKUP_FILE);
        temp.createNewFile();
        UnpackUtil.unpackFromStreamToFile(context.getContentResolver().openInputStream(backupUri), temp);

        ZipFile zipFile = new ZipFile(temp);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        ZipEntry entry;
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            if (isTaskFile(entry.getName())) {
                mTasksFromBackup = readTasks(zipFile.getInputStream(entry));
            } else {
                File photoFile = repository.getFile(entry.getName());
                photoFile.createNewFile();
                UnpackUtil.unpackFromStreamToFile(zipFile.getInputStream(entry), photoFile);
            }
        }
        temp.delete();
    }

    private List<Task> readTasks(InputStream entryStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(entryStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String jsonString = JsonFileContentToStringParser.getJsonString(reader);

        Gson gson = new GsonBuilder().create();
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();

        return gson.fromJson(jsonString, taskListType);
    }
}
