package com.a_team.taskmanager.backup;

import android.content.Context;
import android.util.Log;

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.a_team.taskmanager.backup.utils.BackupConstants.BACKUP_FILE;
import static com.a_team.taskmanager.backup.utils.BackupConstants.BACKUP_FOLDER;
import static com.a_team.taskmanager.backup.utils.BackupConstants.FILE_NOT_FOUND_WRITE;
import static com.a_team.taskmanager.backup.utils.BackupConstants.IO_EXCEPTION_WRITE;
import static com.a_team.taskmanager.backup.utils.BackupConstants.TASKS_FILENAME;

public class FileDataWriter {
    private List<File> mFiles;

    public FileDataWriter() {
    }

    public Observable<File> writeTasksToBackup(Context context, TaskManagerRepository repository, List<Task> tasks) throws IOException {
        return Observable.fromCallable(() -> {
            mFiles = new ArrayList<>();
            File backup = getBackupFile(context);

            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backup))) {
                writeTasksToFiles(repository, tasks);
                zipFiles(zos);
            } catch (FileNotFoundException e) {
                throw new IOException(FILE_NOT_FOUND_WRITE, e);
            } catch (IOException e) {
                throw new IOException(IO_EXCEPTION_WRITE, e);
            }

            Log.i("Backup", "tasks stored. Thread: " + Thread.currentThread().getName());

            return backup;
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private File getBackupFile(Context context) throws IOException {
        File backupDir = new File(context.getExternalFilesDir(null), BACKUP_FOLDER);
        if (!backupDir.exists())
            backupDir.mkdir();

        File backup = new File(backupDir, BACKUP_FILE);
        backup.createNewFile();

        return backup;
    }

    private void writeTasksToFiles(TaskManagerRepository repository, List<Task> tasks) throws IOException {
        File json = repository.getFile(TASKS_FILENAME);
        writeTasksToJson(tasks, json);
        mFiles.add(json);
        for (Task task : tasks) {
            if (task != null) {
                addPhotoFileToList(repository, task);
            }
        }
    }

    private void writeTasksToJson(List<Task> tasks, File tasksJson) throws IOException {
        try (FileWriter fileWriter = new FileWriter(tasksJson)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type taskListType = new TypeToken<List<Task>>() {
            }.getType();
            gson.toJson(tasks, taskListType, fileWriter);
        }
    }

    private void addPhotoFileToList(TaskManagerRepository repository, Task task) {
        File photoFile = repository.getFile(task.getPhotoFilename());
        if (photoFile != null && photoFile.exists()) {
            mFiles.add(photoFile);
        }
    }

    private void zipFiles(ZipOutputStream zos) throws IOException {
        for (File photoFile : mFiles) {
            writeFileToZip(zos, photoFile);
        }
    }

    private void writeFileToZip(ZipOutputStream zos, File file) throws IOException {
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
