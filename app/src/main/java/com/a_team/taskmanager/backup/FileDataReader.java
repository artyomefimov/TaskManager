package com.a_team.taskmanager.backup;

import com.a_team.taskmanager.entity.Task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.a_team.taskmanager.backup.BackupConstants.FILE_NOT_FOUND_WRITE;
import static com.a_team.taskmanager.backup.BackupConstants.READ_TASK_EXCEPTION;

public class FileDataReader {
    private static final String TAG = "FileDataReader";

    public List<Task> readTasksFromBackup(File file) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry;
            Task task;
            byte[] buffer = new byte[1024];
            int length;
            File tempFile;
            while ((zipEntry = zis.getNextEntry()) != null) {
                tempFile = new File(zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(tempFile);
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
                task = (Task) ois.readObject();
                tasks.add(task);
                tempFile.delete();
            }
        } catch (FileNotFoundException e) {
            throw new IOException(FILE_NOT_FOUND_WRITE, e);
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException(READ_TASK_EXCEPTION, e);
        }
        return tasks;
    }
}
